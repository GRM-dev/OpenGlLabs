#include "texture.h"

//--------------------------------------------------------------------------------------------
// Domyslny konstruktor 
glTexture::glTexture(char *FileName)
{
	// Wpisz kod tutaj 
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor 
glTexture::~glTexture()
{
	glDeleteSamplers(1, &SamplerId);
	glDeleteTextures(1, &TextureId);
}
//--------------------------------------------------------------------------------------------
void glTexture::LoadBMP(char *filename,int &_width, int &_height, char **pixels)
{
	char buf[100];  // 100B temporary buffer

	// some essential header fields
	// to be compared against the assumed bmp format
	int file_actual_size;
	int file_size;  
	int bmp_compression;

	// file handle
	FILE *fil =  NULL;
	fopen_s(&fil,filename,"rb");
	
	// check if file is opened 
	if (fil == NULL) ThrowException("Brak pliku BMP");

	fseek (fil, 0, SEEK_END);
    file_actual_size=ftell (fil); // obtain real filesize 
	rewind(fil); // make sure the file pointer is set to the beginning 

	//*********** READ THE HEADER ***************
	
	// read the signature 
	fread(buf,1,2,fil); 	
	if ((buf[0] !=0x42) || (buf[1] != 0x4d)) {fclose(fil); ThrowException("Invalid signature");}

	// read the filesize
	fread(&file_size,sizeof(file_size),1,fil);
	if (file_size != file_actual_size) {ThrowException("Invalid file size");}

	// 12 dummies
	fread(buf,1,12,fil);

	fread(&_width,sizeof(_width),1,fil); 
	fread(&_height,sizeof(_height),1,fil); 
	
	// 2 dummies
	fread(buf,1,2,fil);

	// bits per pixel
	fread(buf,1,1,fil);
	if (buf[0] != 24) ThrowException("Invalid BPP"); 

	// 1 dummy
	fread(buf,1,1,fil);

	//compression mode 
	fread(&bmp_compression,sizeof(bmp_compression),1,fil); 
	if (bmp_compression != 0) ThrowException("Invalid Compression");; // invalid compression

	// 20 dummies
	fread(buf,1,20,fil);

	//********************* END OF THE HEADER ***********************

	// determine padding constant 
	int padding = 0;
	if (_width*3 % 4) padding = 4 - (_width*3) % 4;

	// allocate memory for pixeltable 
	*pixels  = (char *)calloc((_width*3+padding)*_height,sizeof(char));

	// read file content into pixeltable
	for (int h = 0; h < _height; h++)
	{
		fread(*pixels+h*(_width*3+padding),1,_width*3+padding,fil);
	}
	
	fclose(fil);
}
//--------------------------------------------------------------------------------------------
// ustawia teksture jako aktualna w OpenGL
GLuint glTexture::Bind()
{
	glActiveTexture(GL_TEXTURE0); // poziom mipmapy 
	glBindTexture(GL_TEXTURE_2D, TextureId);
	glBindSampler(0, SamplerId);
	return TextureId;
}
//--------------------------------------------------------------------------------------------
// The end 


