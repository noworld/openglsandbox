#VBO/IBO/DSC Model Writing from Blender
#Custom Properties:
#scene.hzg_file_name - name of zip file to write
#object.hzg_type - type of object to be populated by the reading program
#object.hzg_export_mode - how to export the data for this object
#	- POSITION_ONLY - Write tightly packed vertex position data, no normals or texture coordinates
#	- VNC - Write packed Vertex/Normal/TexCoord data in VVVNNNCC format
#
import bpy
import bmesh
import zipfile
import struct
import sys
import os
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
out_path = "C:\\Users\\nicholas.waun\\git\\openglsandbox\\SimpleRender2\\res\\raw\\"
work_path = proj_path+"\\res\\work\\"
dsc_ext = ".dsc"
vbo_ext = ".v"
ibo_ext = ".i"
def write_mesh_files(obj, scene):
    scene.objects.active = obj
    file_name = obj.name
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
   
    if(uv_lay is None):
        print("UV coords will not be exported for",obj.name)
        nrm_offset = 3 * bpf
        txc_offset = -1
        stride = 6 * bpf
    else:
        nrm_offset = 3 * bpf
        txc_offset = 6 * bpf
        stride = 8 * bpf
    vboBytes = bytearray(len(bm.faces)*3*stride)
    iboBytes = bytearray()
    dscString = "OBJECT_NAME=%s" % obj.name
    dscString += "\nOBJECT_TYPE=GAME_ENTITY"
    dscString += "\nNUM_ELEMENTS=%i" % (len(bm.faces)*3)
    dscString += "\nPOS_OFFSET=0"
    dscString += "\nNRM_OFFSET=%i" % nrm_offset
    dscString += "\nTXC_OFFSET=%i" % txc_offset
    dscString += "\nELEMENT_STRIDE=%i" % stride
    dscString += "\nNRM_SIZE=3"
    dscString += "\nPOS_SIZE=3"
    dscString += "\nTXC_SIZE=2"
    print("**** STRIDE IS:",stride)
    for face in bm.faces:
        print("**** Face #",face.index)
        for loop in face.loops:
            print("**** Loop vert #",loop.vert.index)
            pos = bm.verts[loop.vert.index].co
            print("**** Vert: %.5f,%.5f,%.5f" % (pos.x,pos.y,pos.z))
            if(round_verts):
                struct.pack_into(">fff", vboBytes, loop.vert.index * stride, round(pos.x,5), round(pos.y,5), round(pos.z,5))
            else:
                struct.pack_into(">fff", vboBytes, loop.vert.index * stride, pos.x,pos.y,pos.z)
            
            nrm = bm.verts[loop.vert.index].normal
            print("**** Normal: %.4f,%.4f,%.4f" % (nrm.x, nrm.y, nrm.z))
            struct.pack_into(">fff", vboBytes, (loop.vert.index * stride) + nrm_offset, nrm.x,nrm.y,nrm.z)
            
            if(uv_lay is not None):
                txc = loop[uv_lay].uv
                txc_x = txc.x
                txc_y = txc.y
                txc_index = (loop.vert.index * stride) + txc_offset
                print("**** TXC Index:",txc_index)
                print("**** Texture Coords (before):", txc_x, ",", txc_y)
                struct.pack_into(">ff", vboBytes, txc_index, txc_x, txc_y)
                print("**** Texture Coords (after):", txc_x, ",", txc_y)
            
            iboBytes += struct.pack(">h",loop.vert.index)
    bm.free()
    
    ctr = 0
    print("Counting to:",len(vboBytes))
    while ctr < len(vboBytes):
        px,py,pz,nx,ny,nz,tx,ty = struct.unpack_from(">8f", vboBytes, ctr)        
        i = ctr/stride
        print("*SKYBOX VERT:",i)
        print("**SKYBOX POS[", ctr , "]:", px)
        print("**SKYBOX POS[", ctr+4  , "]:", py)
        print("**SKYBOX POS[", ctr+8 , "]:", pz)
        print("**SKYBOX NRM[", ctr+12 , "]:", nx)
        print("**SKYBOX NRM[", ctr+16 , "]:", ny)
        print("**SKYBOX NRM[", ctr+20 , "]:", nz)
        print("**SKYBOX TXC[", ctr+24 , "]:", tx)
        print("**SKYBOX TXC[", ctr+28 , "]:", ty)
        ctr += stride
    
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
                zf.write(work_path + "index", "index", compress_type=compression)
                print("")
            else:
                print("Not including",obj.name,"type is",obj.type,"visibility is",obj.is_visible(scene))
        zf.close()
if(clear_work_directory):
    print("not implemented")
