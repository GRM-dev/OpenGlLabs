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

	GLuint texture_id = tex->Bind();
	BeginObject(GL_TRIANGLE_STRIP, texture_id);

	SetColor(1.0, 1.0, 1.0);

	while (kat <= 360.0)
	{
		AddVertex(R*cos(kat*PI / 180.0), H / 2.0, R*sin(kat*PI / 180.0), 1.0, kat / 360);
		AddVertex(R*cos(kat*PI / 180.0), -H / 2.0, R*sin(kat*PI / 180.0), 0.0, kat / 360);
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
