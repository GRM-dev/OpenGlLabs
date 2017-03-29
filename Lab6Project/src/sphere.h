#ifndef sphere_H
#define sphere_H

#include "globject.h"
#include "texture.h"
#include "common.h"

class glSphere: public glObject
{
	public: 
		glSphere(float R, char *TextureFile); // domyslny konstruktor 
		~glSphere(); // domyslny destrutor 
	private:	
		glTexture *tex;
};

#endif
