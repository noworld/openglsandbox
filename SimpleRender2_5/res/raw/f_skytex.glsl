precision mediump float;

const float c_SunAngle = 20.0;

uniform sampler2D u_Texture;
  
varying vec3 v_Position;
varying vec3 v_Normal;
varying vec2 v_TexCoordinate;

void main()                    		
{                  
	//txc is -pi -> pi
    float sin_dec = sin(radians(c_SunAngle));
    float sin_x = sin(v_TexCoordinate.s);
    float sin_y = sin(v_TexCoordinate.t);
    
    if(sin_dec - (abs(sin_x) - abs(sin_y)) < 0.02) {
        gl_FragColor = vec4(1.0,0.0,0.0,1.0);
    } else {
        gl_FragColor = vec4(1.0,1.0,1.0,1.0);
    }
    
     //gl_FragColor = texture2D(u_Texture, v_TexCoordinate);                     		
}                                                                     	

