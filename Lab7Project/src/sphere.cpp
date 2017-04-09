#include "sphere.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glSphere::glSphere(float R, char *TextureFile)
{
	tex = nullptr;
	tex = new glTexture(TextureFile);

	// przygotuj geometrie obiektu
	float kat = 0.0;
	int slices = 50;
	float dk = 360.0 / float(slices - 1);

	BeginObject(GL_TRIANGLE_STRIP, tex->Bind());


	SetColor(1.0, 1.0, 1.0);

	float u = 0.0;
	float du = 1.0 / float(slices - 1);

	float v = 0.0;
	float dv = 1.0 / float(slices - 1);

	float phi = -90.0;
	float dphi = 180.0 / float(slices - 1);

	int cl = 0;

	while (phi <= 90)
	{
		kat = 0.0;
		u = 0.0;
		while (kat <= 360.0)
		{
			SetNormal(cos((phi + dphi)*PI / 180.0)*cos(kat*PI / 180.0), sin((phi + dphi)*PI / 180.0), cos((phi + dphi)*PI / 180.0)*sin(kat*PI / 180.0));
			AddVertex(R*cos((phi + dphi)*PI / 180.0)*cos(kat*PI / 180.0), R*sin((phi + dphi)*PI / 180.0), R*cos((phi + dphi)*PI / 180.0)*sin(kat*PI / 180.0), u, v + dv);
			SetNormal(cos(phi*PI / 180.0)*cos(kat*PI / 180.0), sin(phi*PI / 180.0), cos(phi*PI / 180.0)*sin(kat*PI / 180.0));
			AddVertex(R*cos(phi*PI / 180.0)*cos(kat*PI / 180.0), R*sin(phi*PI / 180.0), R*cos(phi*PI / 180.0)*sin(kat*PI / 180.0), u, v);
			kat += dk;
			u += du;
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
