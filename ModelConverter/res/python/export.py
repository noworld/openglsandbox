#VBO/IBO/DSC Model Writing from Blender
#Custom Properties:
#scene.hzg_file_name - name of zip file to write

import bpy
import bmesh
import zipfile
import sys

try:
    import zlib
    compression = zipfile.ZIP_DEFLATED
except:
    compression = zipfile.ZIP_STORED


out_path = "C:\\Users\\nicholas.waun\\git\\openglsandbox\\ModelConverter\\res\\out\\"
work_path = "C:\\Users\\nicholas.waun\\git\\openglsandbox\\ModelConverter\\res\\work\\"
dsc_ext = ".d"
vbo_ext = ".v"
ibo_ext = ".i"

def write_mesh_files(obj, scene):
    file_name = obj.name
    bm = bmesh.new()
    bm.from_mesh(obj.data)
    print("MESH CREATED")
    print("NUM VERTS:",len(bm.verts))
    print("MESH VOLUME:",bm.calc_volume(False))
    f = open(work_path + file_name + dsc_ext, 'w')
    f.close()
    f = open(work_path + file_name + vbo_ext, 'w')
    f.close()
    f = open(work_path + file_name + ibo_ext, 'w')
    f.close()
    bm.free()
    return file_name


#**********************************************************
print("\n*** Welcome to Blender Python Zip Exporter. ***\n");
print("Running in",sys.argv[0])

for scene in bpy.data.scenes:
	print("* Operating on Scene:",scene.name)
	print("* Using zip file",scene["hzg_file_name"],"\n")
	zf = zipfile.ZipFile(out_path + scene["hzg_file_name"], mode='w')
	for obj in scene.objects:
	    if(obj.type == "MESH"):
	        print("** Writing",obj.name)
	        file_name = write_mesh_files(obj, scene)	    
	        zf.write(work_path + file_name + dsc_ext, file_name + dsc_ext, compress_type=compression)   
	        zf.write(work_path + file_name + vbo_ext, file_name + vbo_ext, compress_type=compression)
	        zf.write(work_path + file_name + ibo_ext, file_name + ibo_ext, compress_type=compression)
	        print("")
	    else:
	        print("Not including",obj.name,"type is",obj.type)
	zf.close()
	
	




