#VBO/IBO/DSC Model Writing from Blender
#Custom Properties:
#scene.hzg_file_name - name of zip file to write
#object.hzg_type - type of object to be populated by the reading program
#	- Defaults to "ENTITY"
#	- GEO_MIPMAP - Used for terrain geometry
#   - UI_ELEMENT - Drawn in the same position in front of the camera as UI elements
#   - INPUT_AREA - Polygons drawn over UI area to detect input 
#object.hzg_export_mode - how to export the data for this object
#	- V   - Write tightly packed vertex position data, no normals or texture coordinates (VVV)
#	- VC  - Write Vertex position and Texture Coordinates in VVVCC format.
#	- VNC - Write packed Vertex/Normal/TexCoord data in VVVNNNCC format
#object.hzg_round - integer, how many places to round decimals
#object.hzg_texture - texture name to use

import sysconfig
print(sysconfig.get_config_h_filename())

import bpy
import bmesh
import zipfile
import struct
import sys
import os
import mathutils
from scipy.spatial import ConvexHull

try:
    import zlib
    compression = zipfile.ZIP_DEFLATED
except:
    compression = zipfile.ZIP_STORED
bpf = 4
bpi = 4
bps = 2
clear_work_directory = True
proj_path = os.environ.get("BLENDER_EXPORT_WORK", "C:\\Users\\nicholas.waun\\git\\openglsandbox\\ModelConverter")
#out_path = proj_path+"\\res\\out\\"
out_path = "C:\\Users\\nicholas.waun\\git\\openglsandbox\\SimpleRender2_5\\res\\raw\\"
work_path = proj_path+"\\res\\work\\"
dsc_ext = ".dsc"
vbo_ext = ".v"
ibo_ext = ".i"
    
def cross_z(p1, p2, p3):
    return ((p2.x-p1.x) * (p3.y-p1.y)) - ((p2.y-p1.y) * (p3.x-p1.x));

def write_mesh_files(obj, scene):
    scene.objects.active = obj
    file_name = obj.name
    export_mode = (obj["hzg_export_mode"] or "VNC")
    print("HZG_EXPORT_MODE is",export_mode)
    bpy.ops.object.modifier_apply(modifier='Subsurf')
    round_verts = obj.get("hzg_round",0)
    bm = bmesh.new()
    bm.from_mesh(obj.data)
    bmesh.ops.triangulate(bm, faces=bm.faces)
    print("*** NUM FACES:",len(bm.faces))
    print("*** ROUND VERTEXES:",round_verts)
    #print("MESH VOLUME:",bm.calc_volume(False))
    
    uv_lay = bm.loops.layers.uv.active #This will be None if object is not unwrapped
    print("Active layer:",uv_lay)
   
    if(uv_lay is None or export_mode == "V"):
        print("UV coords will not be exported for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = -1
        stride = 6 * bpf
    elif(export_mode == "VC"):
        print("Normals will not be exported for",obj.name)
        nrm_offset = -1
        txc_offset = 3 * bpf
        stride = 5 * bpf
    else:
        print("Exporting data in VNC format for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = 6 * bpf
        stride = 8 * bpf
        
    vboBytes = bytearray()
    iboBytes = bytearray()
    dscString = "OBJECT_NAME=%s" % obj.name
    dscString += "\nOBJECT_TYPE=%s" % obj.get("hzg_type","ENTITY")
    dscString += "\nTEXTURE=%s" % obj.get("hzg_texture","uvgrid")
    dscString += "\nNUM_ELEMENTS=%i" % (len(bm.faces)*3)
    dscString += "\nPOS_OFFSET=0"
    dscString += "\nNRM_OFFSET=%i" % nrm_offset
    dscString += "\nTXC_OFFSET=%i" % txc_offset
    dscString += "\nELEMENT_STRIDE=%i" % stride
    dscString += "\nNRM_SIZE=3"
    dscString += "\nPOS_SIZE=3"
    dscString += "\nTXC_SIZE=2"
    
    print("Object type for %s is %s" % (obj.name, obj["hzg_type"]))
    if(obj["hzg_type"] == "INPUT_AREA"):
        print("Generating convex hull for %s" % obj.name)
        hull_points= ""
        
        print("VERTS")
        vert_ctr = 0
        for edge in bm.edges:
            for vert in edge.verts:
                print("EDGE Vertex %s : %.5f,%.5f,%.5f" % (vert_ctr, vert.co.x, vert.co.y, vert.co.z))
                #vert.co.z = 0.0
                vert_ctr = vert_ctr + 1
            print("")
        
        hull = bmesh.ops.convex_hull(bm, input=bm.verts, use_existing_faces=True)
        vert_ctr = 0
        vert_0 = "start"
        vert_1 = "end"
        points = []        
        print("- - - - - - - - - - - - - - ")
        print("HULL OBJECT: ",hull["geom"])
        for element in hull["geom"]:     
#            print("Element is:",type(element))       
            if(type(element) == bmesh.types.BMVert):
                vert_ctr = vert_ctr + 1
                print("Vertex %s : %.5f,%.5f,%.5f" % (vert_ctr, element.co.x, element.co.y, element.co.z))
                points.append(element.co)
            elif(type(element) == bmesh.types.BMEdge):
                for vert in element.verts:
                    print("EDGE Vertex %s : %.5f,%.5f,%.5f" % (vert_ctr, vert.co.x, vert.co.y, vert.co.z))
                print("")
            
        #Compare to see if clockwise or ccw
        crossz = cross_z(points[0], points[1], points[2])
        print("CROSS Z TEST: %.5f" % crossz)
        if(crossz < 0):
            for point in points:
                if(hull_points):
                    hull_points = hull_points + ","
                point_string = ("%f,%f,%f") % (point.x, point.y, point.z)
                hull_points = hull_points + point_string
        else:
            for i in range(len(points)-1, 0, -1):
                if(hull_points):
                    hull_points = hull_points + ","
                point_string = ("%f,%f,%f") % (points[i].x, points[i].y, points[i].z)
                hull_points = hull_points + point_string
        
        dscString += "\nHULL=%s" % hull_points 
    
    print("**** STRIDE IS:",stride)
    ctr = 0
    for face in bm.faces:
#        print("**** Face #",face.index)
        for loop in face.loops:
#            print("**** Loop vert #",loop.vert.index)
            pos = bm.verts[loop.vert.index].co
#            print("**** Vert: %.5f,%.5f,%.5f" % (pos.x,pos.y,pos.z))
            if(round_verts):
                vboBytes += struct.pack(">fff", round(pos.x,5), round(pos.y,5), round(pos.z,5))
            else:
                vboBytes += struct.pack(">fff", pos.x,pos.y,pos.z)
            
            if(export_mode == "VNC"):
                nrm = bm.verts[loop.vert.index].normal
#                print("**** Normal: %.4f,%.4f,%.4f" % (nrm.x, nrm.y, nrm.z))
                vboBytes += struct.pack(">fff",nrm.x,nrm.y,nrm.z)
            
            if(uv_lay is not None and export_mode != "V"):
                txc = loop[uv_lay].uv
#                print("**** Texture Coords:", txc.x, ",", txc.y)
                vboBytes += struct.pack(">ff", txc.x, 1-txc.y)
            
            iboBytes += struct.pack(">h",ctr)
            ctr += 1
    bm.free()
    
    fileVbo = open(work_path + file_name + vbo_ext, 'wb')
    fileVbo.write(vboBytes)
    fileVbo.close()
    
    fileIbo = open(work_path + file_name + ibo_ext, 'wb')
    fileIbo.write(iboBytes)
    fileIbo.close()
    
    fileDsc = open(work_path + file_name + dsc_ext, 'w')
    fileDsc.write(dscString)
    fileDsc.close()
    
    idxString = "+" + file_name + "\n"
    idxString += "-" + file_name + vbo_ext + "\n"
    idxString += "-" + file_name + ibo_ext + "\n"
    idxString += "-" + file_name + dsc_ext + "\n"
    
    fileIdx = open(work_path + "index", 'a')
    fileIdx.write(idxString)
    fileIdx.close()
    
    bm.free()
    return file_name
#**********************************************************
print("\n*** Welcome to Blender Python Zip Exporter. ***\n");
print("Running in",sys.argv[0])
print(sys.version_info);
print("")
fileIdx = open(work_path + "index", 'w')
fileIdx.write("")
fileIdx.close()
for scene in bpy.data.scenes:
        print("* Operating on Scene:",scene.name)
        print("* Using zip file",scene["hzg_file_name"],"\n")
        zf = zipfile.ZipFile(out_path + scene["hzg_file_name"], mode='w')
        for obj in scene.objects:
            if(obj.type == "MESH" and obj.is_visible(scene)):
                print("** Writing",obj.name)
                file_name = write_mesh_files(obj, scene)	   
                zf.write(work_path + file_name + dsc_ext, file_name + dsc_ext, compress_type=compression)   
                zf.write(work_path + file_name + vbo_ext, file_name + vbo_ext, compress_type=compression)
                zf.write(work_path + file_name + ibo_ext, file_name + ibo_ext, compress_type=compression)
                print("")
            else:
                print("Not including",obj.name,"type is",obj.type,"visibility is",obj.is_visible(scene))
        zf.write(work_path + "index", "index", compress_type=compression)
        zf.close()
if(clear_work_directory):
    print("not implemented")
