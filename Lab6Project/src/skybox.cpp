#include "skybox.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glSkyBox::glSkyBox(float R, float H, char *TextureFile)
{
	tex = nullptr;
	tex = new glTexture(TextureFile);

	// przygotuj geometrie obiektu
	float kat = 0.0;
	int slices = 50;
	float dk = 360.0 / float(slices - 1);

	BeginObject(GL_LINE_STRIP);

	SetColor(1.0, 1.0, 1.0);

	while (kat <= 360.0)
	{
		AddVertex(R*cos(kat*PI / 180.0), H / 2.0, R*sin(kat*PI / 180.0));
		AddVertex(R*cos(kat*PI / 180.0), -H / 2.0, R*sin(kat*PI / 180.0));
		kat += dk;
	}

	EndObject();
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor
glSkyBox::~glSkyBox()
{
	if (tex) delete tex;
}
//--------------------------------------------------------------------------------------------
// the end
