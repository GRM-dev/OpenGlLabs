#include "printer.h"

inline int next_p2(int n) { int res = 1; while (res < n)res <<= 1; return res; }

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glPrinter::glPrinter(char *FontFile, int iPXSize)
{
	BOOL bError = FT_Init_FreeType(&ftLib);

	bError = FT_New_Face(ftLib, FontFile, 0, &ftFace);
	if (bError)
		ThrowException("Blad ladowania czcionki");

	FT_Set_Pixel_Sizes(ftFace, iPXSize, iPXSize);
	iLoadedPixelSize = iPXSize;


	glGenTextures(128, Tex);
	glGenSamplers(1, &SamplerId);

	CharHeight = 0;
	CharWidth = 0;

	for (int i = 0; i < 128; i++)
	{
		CreateGlyph(i);
	}

	sprintf(_msg, "CharHeight = %d CharWidth = %d", CharHeight, CharWidth);
	PrintLog(_msg);
	FT_Done_Face(ftFace);
	FT_Done_FreeType(ftLib);
}
//--------------------------------------------------------------------------------------------
void glPrinter::CreateGlyph(int iIndex)
{
	FT_Load_Glyph(ftFace, FT_Get_Char_Index(ftFace, iIndex), FT_LOAD_DEFAULT);
	FT_Render_Glyph(ftFace->glyph, FT_RENDER_MODE_NORMAL);
	FT_Bitmap* pBitmap = &ftFace->glyph->bitmap;

	int iW = pBitmap->width, iH = pBitmap->rows;
	int iTW = next_p2(iW), iTH = next_p2(iH); // ustawia rozmiar do nastepnej potegi 2

	GLubyte* bData = new GLubyte[iTW*iTH];
	// Copy glyph data and add dark pixels elsewhere

	for (int ch = 0; ch < iTH; ch++)
	{
		for (int cw = 0; cw < iTW; cw++)
		{
			bData[ch*iTW + cw] = (ch >= iH || cw >= iW) ? 0 : pBitmap->buffer[(iH - ch - 1)*iW + cw];
		}
	}

	// Generate an OpenGL texture ID for this texture
	glBindTexture(GL_TEXTURE_2D, Tex[iIndex]);
	// And create a texture from it

	glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, iTW, iTH, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, bData);

	glSamplerParameteri(SamplerId, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
	glSamplerParameteri(SamplerId, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

	glSamplerParameteri(SamplerId, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
	glSamplerParameteri(SamplerId, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

	delete[] bData;

	// Calculate glyph data
	iAdvX[iIndex] = ftFace->glyph->advance.x >> 6;
	iBearingX[iIndex] = ftFace->glyph->metrics.horiBearingX >> 6;
	iCharWidth[iIndex] = ftFace->glyph->metrics.width >> 6;

	iAdvY[iIndex] = (ftFace->glyph->metrics.height - ftFace->glyph->metrics.horiBearingY) >> 6;
	iBearingY[iIndex] = ftFace->glyph->metrics.horiBearingY >> 6;
	iCharHeight[iIndex] = ftFace->glyph->metrics.height >> 6;

	iNewLine = max(iNewLine, int(ftFace->glyph->metrics.height >> 6));

	BeginObject(GL_TRIANGLE_STRIP, Tex[iIndex]);
	SetColor(1.0, 1.0, 1.0);

	AddVertex(0.0f, float(-iAdvY[iIndex] + iTH), 0.0, 0.0, 1.0);
	AddVertex(0.0f, float(-iAdvY[iIndex]), 0.0, 0.0, 0.0);
	AddVertex(float(iTW), float(-iAdvY[iIndex] + iTH), 0.0, 1.0, 1.0);
	AddVertex(float(iTW), float(-iAdvY[iIndex]), 0.0, 1.0, 0.0);

	EndObject();

	if (iCharWidth[iIndex] > CharWidth) CharWidth = iCharWidth[iIndex];
	if (iCharHeight[iIndex] > CharHeight) CharHeight = iCharHeight[iIndex];
}
//--------------------------------------------------------------------------------------------
glPrinter::~glPrinter()
{

}
//--------------------------------------------------------------------------------------------
void glPrinter::Draw(int cVAO)
{
	if (Textures[cVAO] == 0)
	{
		glDisable(GL_TEXTURE_2D);
	}
	else
	{
		glBindTexture(GL_TEXTURE_2D, Textures[cVAO]);
		glBindSampler(0, SamplerId);
		glEnable(GL_TEXTURE_2D);
	}
	glBindVertexArray(VAO[cVAO]);
	glDrawArrays(Primitives[cVAO], 0, lCoords[cVAO] / 3);
	glBindVertexArray(0);
	glDisable(GL_TEXTURE_2D);
}
//--------------------------------------------------------------------------------------------
// the end

