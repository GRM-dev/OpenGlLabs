#ifndef printer_H
#define printer_H

#include "globject.h"
#include "texture.h"
#include "common.h"
#include <ft2build.h>
#include FT_FREETYPE_H

class glPrinter : public glObject
{
public:
	glPrinter(char *FontFile, int iPXSize);
	~glPrinter(); // domyslny destrutor
	void glPrinter::Draw(int cVAO);
	int CharHeight;
	int CharWidth;
	int iAdvX[256], iAdvY[256];
	int iBearingX[256], iBearingY[256];
	int iCharWidth[256], iCharHeight[256];

private:
	FT_Library ftLib;
	FT_Face ftFace;

	UINT Tex[128];
	UINT SamplerId;


	int iLoadedPixelSize, iNewLine;
	void CreateGlyph(int iIndex);
};

#endif
