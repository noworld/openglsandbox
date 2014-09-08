precision mediump float;

struct ellipse {
	float min;
	float maj;
	float minsq;
	float majsq;
	vec2  loc;
	float rfocsq;
	int   oper;
};

const int MAX_ELLIPSES = 100;

uniform sampler2D u_Texture;
uniform int u_NumEllipses;
uniform ellipse[MAX_ELLIPSES] u_Ellipses;
uniform vec4 u_WaterColor;
uniform vec4 u_LandColor;

varying vec2 v_TexCoordinate;  
varying vec3 v_Position;
  
void main()                    		
{
	float inside = 0.0;
	float increment = 1.0 / float(u_NumEllipses);
	                           
	for(int i = 0; i < u_NumEllipses; i++) {
		ellipse ell = u_Ellipses[i];
		
		float xh = v_Position.x - ell.loc.x;
		float yk = v_Position.y - ell.loc.y;				
		float rx = ell.majsq;
		float ry = ell.minsq;

		if( ((xh*xh)/(rx)) + ((yk*yk)/(ry))  <= 1.0) {
			inside = inside + increment;
		}
		
	}
	
	if(inside == increment){
		gl_FragColor = u_LandColor + inside;
	} else {
		gl_FragColor = u_WaterColor;
	}
}                                                               	

