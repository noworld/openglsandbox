#VBO/IBO/DSC Model Writing from Blender

import zipfile
import struct
import sys
import os
try:
    import zlib
    compression = zipfile.ZIP_DEFLATED
except:
    compression = zipfile.ZIP_STORED
    

print("\n*** Welcome to Tiger/Line Zip Reader. ***\n")
print("Running in",sys.argv[0])
print(sys.version_info);
print("")
#fmt_a = ">iiiiiii"
#fmt_a = ">i"
#fmt_b = "<iidddddddd"
zf = zipfile.ZipFile("C:\\Users\\nicholas.waun\\Downloads\\tl_2014_us_zcta510.zip")
data = zf.open("tl_2014_us_zcta510.shp");
#data = zf.open("tl_2014_us_zcta510.shx");
#print("9994")
#print(struct.pack(">i",9994))
#print("/9994")
#print(struct.unpack(">iiiiiiii",bytearray(b"\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x00\x18\xf2\xad\xea\xe8\x03\x00\x00\x05\x00\x00\x00")))
#print(b"\x5b\x5d")
records = ""
try:
#    for line in data:
    for x in range(0,10):
        line = data.readline()
        values = line.strip()
        print(len(values))
        print(values)
        try:
        	records += line
            if(len(line) > 96):
                file_code = struct.unpack(">i",bytearray(line[:4]))
                print("file code:",file_code)
                unused = struct.unpack(">iiiii",bytearray(line[:20]))
                print("unused:",unused)
                file_length = struct.unpack(">i",bytearray(line[20:24]))
                print("file length:",file_length)
                version = struct.unpack("<i",bytearray(line[24:28]))
                print("version",version)
                shape_type = struct.unpack("<i",bytearray(line[28:32]))
                print("shape type:",shape_type)
                bbox = struct.unpack("<dddd",bytearray(line[32:64]))
                print("bounding box:",bbox)
                bbox = struct.unpack("<dddd",bytearray(line[64:96]))
                print("bounding box (z and m):",bbox)
                #record_header = struct.unpack(">ii",bytearray(line[96:104]))
                #print("record header:",record_header)
            
        except:
            print(sys.exc_info()[0])
finally: 
    data.close()
zf.close()

