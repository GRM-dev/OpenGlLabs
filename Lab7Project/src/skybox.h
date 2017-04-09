#ifndef skybox_H
#define skybox_H

#include "globject.h"
#include "texture.h"
#include "common.h"

class glSkyBox : public glObject
{
public:
	glSkyBox(float R, float H, char *TextureFile); // domyslny konstruktor
	~glSkyBox(); // domyslny destrutor
private:
	glTexture *tex;
};

#endif
