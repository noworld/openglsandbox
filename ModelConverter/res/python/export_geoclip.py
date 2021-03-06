#Clipmap Generator for geomipmapping with clipmaps in OpenGL
#http://http.developer.nvidia.com/GPUGems2/gpugems2_chapter02.html

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

#Used in size calculations
bpf = 4 #bytes per float
bpi = 4 #bytes per int
bps = 2 #bytes per short

#Paths
home_path = "C:" + os.environ.get("HOMEPATH","\\Users\\nicholas.waun")
proj_path = home_path + "\\git\\openglsandbox\\ModelConverter"
out_path = home_path + "\\git\\openglsandbox\\SimpleRender2_5\\res\\raw\\"
work_path = proj_path + "\\res\\work\\"

#File extensions
dsc_ext = ".dsc"
vbo_ext = ".v"
ibo_ext = ".i"
zip_ext = ".zip"
file_name = "geomip"

#Grid settings
e_res_exp = 5                      #edge resolution of the L0 clipmap will be (2^exp - 1)
e_res = math.pow(2,e_res_exp) - 1  #n resolution of one edge
#mm_level = 2                       #mip levels 0-mm_level will be generated
s_len = 20.0                       #length of one side of the L0 mesh
h_len = s_len / 2.0                #half the length
blk_sz = int((e_res + 1) / 4)           #m block size of (n + 1)/4
pos_step = s_len / (e_res - 1)  #distance between vertexes in the grid
txc_step = 1 / (e_res - 1)         #distance between texture coordinates

#General object data
file_name = "geoclip"

#Settings for reading geometry into the program
pos_size = 3          #2 = XZ, 3 = XYZ
txc_size = 2          #UV
nrm_offset = -1       #normals not generated
txc_offset = pos_size * bpf            #texture coords after position data
stride = (txc_size + pos_size) * bpf   #3 positions (x,y,z), 2 coords
block_index = 0       #index in the buffer of the block geometry


#**********************************************************
#*****                  FUNCTIONS                     *****
#**********************************************************
    
#***
#Write the descriptor file to the work directory
def write_geometry_data():
    vboBytes = bytearray()
    iboBytes = bytearray()
    dat_ctr = 0
    idx_ctr = 0

    #generate block data
    blk_num_elements = 0
    print("Data for BLOCK")
    x_sz = blk_sz
    z_sz = blk_sz
    for x in range(x_sz):
        for z in range(z_sz):
            bx = x * pos_step
            by = 0.0
            bz = z * pos_step
            u = x * txc_step
            v = z * txc_step
            print("VERT %i,%i: %.3f,%.3f / %.3f,%.3f" % (x,z,bx,bz,u,v))
            vboBytes += struct.pack(">fff",bx,by,bz)
            vboBytes += struct.pack(">ff",u,1.0-v)
            dat_ctr = dat_ctr + 5

    #generate block indexes
    for i in range(z_sz-1):
        degen = 0
        a_degen = 0
        for j in range(x_sz):
            if(i % 2 == 0):
                up_index = j + (i * x_sz)
                degen = up_index
                alt_up_index = up_index + x_sz
                a_degen = alt_up_index                
                if(i == 0 or (i > 0 and j > 0)):
                    print("  up:",up_index)
                    iboBytes += struct.pack(">h",up_index)
                    idx_ctr = idx_ctr + 1
                print("a_up:",alt_up_index)
                iboBytes += struct.pack(">h",alt_up_index)
                idx_ctr = idx_ctr + 1
            else:
                dn_index = (i * x_sz) + x_sz - 1 - j
                if(j > 0):
                    degen = dn_index
                    print("  dn:",dn_index)
                    iboBytes += struct.pack(">h",dn_index)
                    idx_ctr = idx_ctr + 1
                alt_dn_index = ((i + 1) * x_sz) + x_sz - 1 - j
                a_degen = alt_dn_index
                print("a_dn:",alt_dn_index)
                iboBytes += struct.pack(">h",alt_dn_index)
                idx_ctr = idx_ctr + 1
        if(i < z_sz - 2):
            print("_dgn",degen)
            print("_dgn",a_degen)
            iboBytes += struct.pack(">hh",degen,a_degen)
            idx_ctr = idx_ctr + 2
            
    blk_num_elements = idx_ctr

    #generate ring fill data
    print("\nData for RING FILL")
    print("RING FILL offset:",int(idx_ctr * bps))
    print("RING FILL DATA offset:",int(dat_ctr / 5))
    ring_fill_index_count = idx_ctr
    ring_fill_index = int(idx_ctr * bps)
    ring_fill_data_index = int(dat_ctr / 5)
    x_sz = 3
    z_sz = blk_sz
    for z in range(z_sz):
        for x in range(x_sz):
            bx = x * pos_step
            by = 0.0
            bz = z * pos_step
            u = x * txc_step
            v = z * txc_step
            print("VERT %i,%i: %.3f,%.3f / %.3f,%.3f" % (x,z,bx,bz,u,v))
            vboBytes += struct.pack(">fff",bx,by,bz)
            vboBytes += struct.pack(">ff",u,1.0-v)
            dat_ctr = dat_ctr + 5

    #generate ring fill indexes 
    for i in range(z_sz-1):
        degen = 0
        a_degen = 0
        for j in range(x_sz):
            if(i % 2 == 0):
                up_index = j + (i * x_sz) + ring_fill_data_index
                degen = up_index
                alt_up_index = up_index + x_sz
                a_degen = alt_up_index                
                if(i == 0 or (i > 0 and j > 0)):
                    print("  up:",up_index)
                    iboBytes += struct.pack(">h",up_index)
                    idx_ctr = idx_ctr + 1
                print("a_up:",alt_up_index)
                iboBytes += struct.pack(">h",alt_up_index)
                idx_ctr = idx_ctr + 1
            else:
                dn_index = (i * x_sz) + x_sz - 1 - j + ring_fill_data_index
                if(j > 0):
                    degen = dn_index
                    print("  dn:",dn_index)
                    iboBytes += struct.pack(">h",dn_index)
                    idx_ctr = idx_ctr + 1
                alt_dn_index = ((i + 1) * x_sz) + x_sz - 1 - j + ring_fill_data_index
                a_degen = alt_dn_index
                print("a_dn:",alt_dn_index)
                iboBytes += struct.pack(">h",alt_dn_index)
                idx_ctr = idx_ctr + 1
        if(i < z_sz - 2):
            print("_dgn",degen)
            print("_dgn",a_degen)
            iboBytes += struct.pack(">hh",degen,a_degen)
            idx_ctr = idx_ctr + 2
    ring_fill_num_el = idx_ctr - ring_fill_index_count
    print("RING FILL NUM ELEMENTS:",ring_fill_num_el)

    #generate interior trim data
    print("\nData for INTERIOR TRIM")
    print("INTERIOR FILL offset:",int(idx_ctr * bps))
    print("INTERIOR FILL DATA offset:",int(dat_ctr / 5))
    int_fill_index_count = idx_ctr
    int_fill_index = int(idx_ctr * bps)
    int_fill_data_index = int(dat_ctr / 5)
    x_sz = (2 * blk_sz) + 1
    z_sz = 2
    for z in range(z_sz):
        for x in range(x_sz):
            bx = x * pos_step
            by = 0.0
            bz = z * pos_step
            u = x * txc_step
            v = z * txc_step
            print("VERT %i,%i: %.3f,%.3f / %.3f,%.3f" % (x,z,bx,bz,u,v))
            vboBytes += struct.pack(">fff",bx,by,bz)
            vboBytes += struct.pack(">ff",u,1.0-v)
            dat_ctr = dat_ctr + 5
            
    x_disp = pos_step * ((2 * blk_sz) - 1)
    z_disp = pos_step * 2
    x_sz = 2
    z_sz = (2 * blk_sz) - 1
    for z in range(z_sz):
        for x in range(x_sz):
            bx = (x * pos_step) + x_disp
            by = 0.0
            bz = z * pos_step + z_disp
            u = x * txc_step + (txc_step * z_sz)
            v = z * txc_step + (txc_step * x_sz)
            print("VERT %i,%i: %.3f,%.3f / %.3f,%.3f" % (x,z,bx,bz,u,v))
            vboBytes += struct.pack(">fff",bx,by,bz)
            vboBytes += struct.pack(">ff",u,1.0-v)
            dat_ctr = dat_ctr + 5

    #generate interior trim indexes 
    x_sz = (2 * blk_sz) + 1
    z_sz = 2
    for i in range(z_sz):
        degen = 0
        a_degen = 0
        for j in range(x_sz):
            if(i % 2 == 0):
                up_index = j + (i * x_sz) + int_fill_data_index                
                alt_up_index = up_index + x_sz
                degen = alt_up_index - 1
                a_degen = alt_up_index
                if(i == 0 or (i > 0 and j > 0)):
                    print("  up:",up_index)
                    iboBytes += struct.pack(">h",up_index)
                    idx_ctr = idx_ctr + 1
                print("a_up:",alt_up_index)
                iboBytes += struct.pack(">h",alt_up_index)
                idx_ctr = idx_ctr + 1
            elif(j < (2 * blk_sz) - 1):
                dn_index = (blk_sz * 4) + (j * 2) + 3 + int_fill_data_index
                alt_dn_index = dn_index - 1
                print("  dn:",dn_index)
                print("a_dn:",alt_dn_index)
                iboBytes += struct.pack(">hh",dn_index,alt_dn_index)
                idx_ctr = idx_ctr + 2                

        if(i < z_sz - 1):
            print("_dgn",a_degen)
            print("_dgn",degen)
            iboBytes += struct.pack(">hh",a_degen,degen)
            idx_ctr = idx_ctr + 2
            
    int_fill_num_el = idx_ctr - int_fill_index_count
    print("INTERIOR TRIM NUM ELEMENTS:",int_fill_num_el)
    
    print("FINAL IDX COUNT:",idx_ctr)
    print("FINAL DAT COUNT:",dat_ctr)
    
    print("EXPECTED IBO LENGTH:",idx_ctr * 2)
    print("EXPECTED VBO LENGTH:",dat_ctr * 4)
    
    print("LEN VBO:",len(vboBytes))
    print("LEN IBO:",len(iboBytes))

    #write the work files
    fileVbo = open(work_path + file_name + vbo_ext, 'wb')
    fileVbo.write(vboBytes)
    fileVbo.close()
    fileIbo = open(work_path + file_name + ibo_ext, 'wb')
    fileIbo.write(iboBytes)
    fileIbo.close()
    
    return [ring_fill_index,int_fill_index,ring_fill_num_el,int_fill_num_el,blk_num_elements]

#***
#Write the descriptor file to the work directory
def write_descriptor(ring_fill_index,int_trim_index,ring_fill_num_el,interior_trim_num_el,blk_num_elements):
    dscString = "OBJECT_NAME=%s" % file_name
    dscString += "\nOBJECT_TYPE=GEO_MIPMAP"
    dscString += "\nSHADER=clipmap_shader"
    dscString += "\nPOS_OFFSET=0"
    dscString += "\nNRM_OFFSET=%i" % nrm_offset
    dscString += "\nTXC_OFFSET=%i" % txc_offset
    dscString += "\nELEMENT_STRIDE=%i" % stride
    dscString += "\nNUM_ELEMENTS=%i" % blk_num_elements
    dscString += "\nNRM_SIZE=-1"
    dscString += "\nPOS_SIZE=%i" % pos_size
    dscString += "\nTXC_SIZE=%i" % txc_size
    dscString += "\nSIDE_LENGTH=%i" % (s_len)
    dscString += "\nRESOLUTION=%i" % (e_res)
    dscString += "\nBLOCK_INDEX=0"
    dscString += "\nRING_FILL_INDEX=%i" % (ring_fill_index)
    dscString += "\nRING_FILL_NUM_ELEMENTS=%i" % ring_fill_num_el
    dscString += "\nINTERIOR_TRIM_INDEX=%i" % (int_trim_index)
    dscString += "\nINTERIOR_TRIM_NUM_ELEMENTS=%i" % interior_trim_num_el
    
    fileDsc = open(work_path + file_name + dsc_ext, 'w')
    fileDsc.write(dscString)
    fileDsc.close()
    return
    
#***
#Write the index file to the work directory
def write_index_file():
    idxString = "+" + file_name + "\n"
    idxString += "-" + file_name + vbo_ext + "\n"
    idxString += "-" + file_name + ibo_ext + "\n"
    idxString += "-" + file_name + dsc_ext + "\n"
    
    fileIdx = open(work_path + "index", 'a')
    fileIdx.write(idxString)
    fileIdx.close()
    return

#***
#Write the data files to the work directory
def write_work_files():
    (ring_fill_index,int_fill_index,ring_fill_num_el,interior_trim_num_el,blk_num_elements) = write_geometry_data()
    write_descriptor(ring_fill_index,int_fill_index,ring_fill_num_el,interior_trim_num_el,blk_num_elements)
    write_index_file()
    return

#***    
#Pack the work data into a zip file
def pack_zip_file():
    return


#**********************************************************
#*****                    MAIN                        *****
#**********************************************************
#Log running environment
print("\n*** Welcome to Python Clip Map Creator. ***\n");
print("Running in",sys.argv[0])
print(sys.version_info);

#Log program parameters
#print("Generating mips 0 --",mm_level)
print("Max edge resolution:",e_res)
print("Mesh side length:",s_len)
print("Block size:",blk_sz)
print("")

#clear the index file
fileIdx = open(work_path + "index", 'w')
fileIdx.write("")
fileIdx.close()

#Write the data to the work directory
write_work_files()

#Pack the data into a zip file
print("Out path:",out_path)
print("File name:",file_name)
zf = zipfile.ZipFile(out_path + file_name + zip_ext, mode='w')
zf.write(work_path + file_name + dsc_ext, file_name + dsc_ext, compress_type=compression)   
zf.write(work_path + file_name + vbo_ext, file_name + vbo_ext, compress_type=compression)
zf.write(work_path + file_name + ibo_ext, file_name + ibo_ext, compress_type=compression)
zf.write(work_path + "index", "index", compress_type=compression)
zf.close()
print("Finished.")
