#Grid Generator for geomipmapping with OpenGL
#Generates a mipmapped square grid with
#mip levels 0 through max_mip_level.
#One mesh is generated
#index buffer contains indexes for drawing all mip levels
#as triangle strips
#equation 1: s = (2^L * n) - (2^L - 1) #number of verts/side for level L with n verts/side at higest mip
#equation 2: e = 2s^2 + s -6 #number of elements for a tristrip grid with s verts/side

import bpy
import bmesh
import zipfile
import struct
import sys
import os
import math
try:
    import zlib
    compression = zipfile.ZIP_DEFLATED
except:
    compression = zipfile.ZIP_STORED
bpf = 4 #bytes per float
bpi = 4 #bytes per int
bps = 2 #bytes per short
clear_work_directory = True
home_path = "C:" + os.environ.get("HOMEPATH","\\Users\\nicholas.waun")
proj_path = home_path + "\\git\\openglsandbox\\ModelConverter"
out_path = home_path + "\\git\\openglsandbox\\SimpleRender2_5\\res\\raw\\"
work_path = proj_path + "\\res\\work\\"
dsc_ext = ".dsc"
vbo_ext = ".v"
ibo_ext = ".i"
zip_ext = ".zip"
file_name = "geomip"
min_edge_res = 3 #Highest mip level will be a 3x3 grid with 9 verts
max_mip_level = 2 #max mip level, min is 0

def get_edge_res(cml, mr):
    #cml - this mip level is actually the compliment of the mip level and the max...
    return (math.pow(2,cml) * mr) - (math.pow(2,cml) - 1)

max_edge_res = get_edge_res(max_mip_level,min_edge_res)
num_verts = max_edge_res * max_edge_res
side_len = 20 #length of a side. Grid will be square

def get_num_elements(cml, mr):
    #cml - this mip level is actually the compliment of the mip level and the max...
    mip_edge_res = get_edge_res(cml,mr)
    #return (2*(mip_edge_res * mip_edge_res)) + mip_edge_res - 6
    return ((2 * mip_edge_res) * (mip_edge_res - 1))

def write_mesh_files():
    nrm_offset = -1 #normals not generated
    txc_offset = 3 * bpf #texture coords after position data
    stride = 5 * bpf #3 positions, 2 coords

    dscString = "OBJECT_NAME=%s" % file_name
    dscString += "\nOBJECT_TYPE=GEO_MIPMAP"
    dscString += "\nPOS_OFFSET=0"
    dscString += "\nNRM_OFFSET=%i" % nrm_offset
    dscString += "\nTXC_OFFSET=%i" % txc_offset
    dscString += "\nELEMENT_STRIDE=%i" % stride
    dscString += "\nNRM_SIZE=3"
    dscString += "\nPOS_SIZE=3"
    dscString += "\nTXC_SIZE=2"
    dscString += "\nMAX_MIP_LEVEL=%i" % (max_mip_level)
    dscString += "\nNUM_ELEMENTS=49" #DEBUG

    offset_counter = 0
    for x in range(max_mip_level + 1):
        mip_level = max_mip_level - x
        num_elements = get_num_elements(mip_level, min_edge_res)
        dscString += "\nNUM_ELEMENTS_L%i=%i" % (x, num_elements)
        dscString += "\nINDEX_OFFSET_L%i=%i" % (x, offset_counter)
        offset_counter += num_elements
    
    #GENERATE VERTEX BUFFER
    vboBytes = bytearray(int(num_verts * stride))
    half_side = side_len/2
    pos_step = side_len / (max_edge_res - 1)
    txc_step = 1 / (max_edge_res - 1)
    ctr = 0
    for i in range(int(max_edge_res)):
        #print("*** ROW",i)
        for j in range(int(max_edge_res)):
            x = -half_side + (j * pos_step)
            y = 0.0
            z = -half_side + (i * pos_step)
            u = i * txc_step
            v = j * txc_step
            #print("VERT %i: %.3f,%.3f,%.3f / %.3f,%.3f" % (j,x,y,z,u,v))
            struct.pack_into(">fff", vboBytes, ctr * stride, x,y,z)
            struct.pack_into(">ff", vboBytes, (ctr * stride) + txc_offset, u, v)
            ctr += 1

    fileVbo = open(work_path + file_name + vbo_ext, 'wb')
    fileVbo.write(vboBytes)
    fileVbo.close()
    
    #GENERATE INDEX BUFFER
    iboBytes = bytearray()
    ctr = 0
    print("max mip level:",max_mip_level)
    print("")
    for i in range(max_mip_level + 1):
        comp_mip_level = max_mip_level - i
        num_elements = get_num_elements(comp_mip_level, min_edge_res)
        edge_res = get_edge_res(comp_mip_level,min_edge_res)
        mip_step = math.pow(2,i)
        
        print("mip level:",i)
        print("num elements:",int(num_elements))
        print("edge res:",int(edge_res))
        print("step:",int(mip_step))
        
        #First element
        iboBytes += struct.pack(">h",0)
        #print("start index:",0)
        ctr = 0
        for j in range(0, int(max_edge_res - 1), int(mip_step)): #max_edge_res-1 because we are doing the triangles between rows
            print("")
            print("row %i / %i" % (j,max_edge_res-1))
            degenerate = 0
            for k in range(0, int(max_edge_res), int(mip_step)):
                if(ctr % 2 == 0):
                    #print("even:",k) #counting up
                    up_index = k + (j * max_edge_res);
                    alt_up_index = up_index + (mip_step * max_edge_res)
                    print("up:",int(up_index))
                    print("a_up:",int(alt_up_index))
                    degenerate = alt_up_index
                else:
                    #print("odd:",k) #counting down                    
                    dn_index = (mip_step * max_edge_res * j) + max_edge_res - 1 - k
                    alt_dn_index = (max_edge_res * j) + max_edge_res - 3 - k
                    print("dn:",int(dn_index))
                    if(k+1 < max_edge_res):
                        print("a_dn:",int(alt_dn_index))
                        
            if(ctr % 2 == 0):
                print("d:", int(degenerate))
            ctr += 1
            
    
    fileIbo = open(work_path + file_name + ibo_ext, 'wb')
    fileIbo.write(iboBytes)
    fileIbo.close()
    
    #GENERATE DESCRIPTOR FILE
    fileDsc = open(work_path + file_name + dsc_ext, 'w')
    fileDsc.write(dscString)
    fileDsc.close()
    
    #GENERATE INDEX FILE
    idxString = "+" + file_name + "\n"
    idxString += "-" + file_name + vbo_ext + "\n"
    idxString += "-" + file_name + ibo_ext + "\n"
    idxString += "-" + file_name + dsc_ext + "\n"
    
    fileIdx = open(work_path + "index", 'a')
    fileIdx.write(idxString)
    fileIdx.close()

    return file_name
#**********************************************************
print("\n*** Welcome to Python GeoMipMapMesh Creator. ***\n");
print("Running in",sys.argv[0])
print(sys.version_info);
print("Generating mips 0 -",max_mip_level)
print("Max edge resolution:",max_edge_res)
print("Num verts in mesh:",num_verts)
print("Mesh side length:",side_len)
print("")
fileIdx = open(work_path + "index", 'w')
fileIdx.write("")
fileIdx.close()
file_name = write_mesh_files()
zf = zipfile.ZipFile(out_path + file_name + zip_ext, mode='w')
zf.write(work_path + file_name + dsc_ext, file_name + dsc_ext, compress_type=compression)   
zf.write(work_path + file_name + vbo_ext, file_name + vbo_ext, compress_type=compression)
zf.write(work_path + file_name + ibo_ext, file_name + ibo_ext, compress_type=compression)
zf.write(work_path + "index", "index", compress_type=compression)
zf.close()
if(clear_work_directory):
    print("not implemented")
