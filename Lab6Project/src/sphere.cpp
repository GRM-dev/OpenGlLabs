#include "sphere.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor 
glSphere::glSphere(float R, char *TextureFile)
{
	tex = NULL;
	tex = new glTexture(TextureFile);

	// przygotuj geometrie obiektu 
	float alpha = 0.0; 
	int slices = 50; 
	float dalpha = 360.0/float(slices-1); 

	BeginObject(GL_TRIANGLE_STRIP,tex->Bind());	
			
	SetColor(1.0,1.0,1.0);

	float u = 0.0; 
	float du = 1.0/float(slices-1);
	
	float v = 0.0; 
	float dv = 1.0/float(slices-1);

	float phi = -90.0;
	float dphi = 180.0/float(slices-1);	

	while (phi <= 90)
	{
		alpha = 0.0;
		u = 0.0;
		while (alpha <= 360.0)
		{
			// TODO: zapisz kod tutaj 			
			alpha += dalpha;
			u+=du;
		}
		phi += dphi;
		v += dv;
	}	
	EndObject();
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor 
glSphere::~glSphere()
{	
	if (tex) delete tex;
}
//--------------------------------------------------------------------------------------------
// the end 
