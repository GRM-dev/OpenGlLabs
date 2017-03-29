#ifndef globject_H
#define globject_H

#include "common.h"
//--------------------------------------------------------------------------------------------
// 								CLASS INTERFACE
//--------------------------------------------------------------------------------------------
class glObject
{

public:

	glObject(); //  domyslny konstruktor
	~glObject(); // domyslny destruktor

	void CleanUp();
	void SetColor(float r, float g, float b); // ustawia biezacy kolor dla grupy wierzcholkow
	void SetNormal(float _nx, float _ny, float _nz); // ustawia aktualna normalna
	void BeginObject(GLenum P, GLuint TextureId = 0); // rozpoczyna generowanie nowego ksztaltu

	void EndObject(); // tworzy obiekt po wypelnieniu wierzcholkami

	void AddVertex(float x, float y, float z, float u = 0.0, float v = 0.0); // dodaje wierzcholek do listy ze wsp. tekstury

	void Draw(); // rysuje obiekt na scenie z u¿yciem zadanego przymitywu

protected:

#define MAX_VAO 10 // maksymalna liczba tablic VAO w obiekcie
#define ATTRIB_PER_VERTEX 4 // liczba atrybutow skojarzonych z wierzcholkiem

	int lVAO; // liczba zdefiniowanych tablic VAO


	GLuint VAO[MAX_VAO]; // id tablic wierzcholkow obiektow
	GLuint VBO[ATTRIB_PER_VERTEX*MAX_VAO]; // id buforow wierzcholkow

	GLenum Primitives[MAX_VAO]; // tablica rodzajow prymitywow skojarzonych z VAO
	GLuint Textures[MAX_VAO]; // tablica identyfikatorow tekstur skojarzonych z VAO

	int lCoords[MAX_VAO]; // liczba wspolrzednych skojarzona z kazda VAO

	float *Coords; // tablica ze wspolrzednymi
	float *Cols; // tablica z kolorami
	float *Normals; // tablica z normalnymi
	float *TexCoords; // tablica ze wspolrzednymi tekstur

	// ustawienia aktualnego koloru
	float col_r;
	float col_g;
	float col_b;

	// ustawienia aktualnej normalnej
	float nx;
	float ny;
	float nz;

	// komunikaty diagnostyczne
	char _msg[1024];

};

#endif