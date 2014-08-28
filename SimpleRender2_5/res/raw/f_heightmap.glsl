precision mediump float;

uniform sampler2D u_Texture;
uniform vec2 u_Dimensions;
uniform vec2 u_LineVec;
uniform float u_Height;
  
varying vec3 v_Position;
varying vec2 v_TexCoordinate;
  
void main()                    		
{                              

	vec3 linev = vec3(u_LineVec, 0.0);
	vec3 diff = v_Position - linev;
	float dir = dot(diff, linev);
	vec4 color = texture2D(u_Texture, v_TexCoordinate);
	
	if(dir > 0.0) {
		vec3 height = vec3(u_Height);
    	gl_FragColor = vec4((color.rgb + height), 1.0);	
	} else {
    	gl_FragColor = color;
	}
               		
}                                                                     	

