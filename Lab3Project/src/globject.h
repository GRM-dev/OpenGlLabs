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

	void SetColor(float r, float g, float b); // ustawia biezacy kolor dla grupy wierzcholkow
	void BeginObject(GLenum P); // rozpoczyna generowanie nowego kszta³tu
	void EndObject(); // tworzy obiekt po wypelnieniu wierzcholkami
	void AddVertex(float x, float y, float z); // dodaje wierzcholek do listy
	void Draw(); // rysuje obiekt na scenie z u¿yciem zadanego przymitywu

private:


	int lVAO; // liczba zdefiniowanych tablic VAO
#define MAX_VAO 10 // maksymalna liczba tablic VAO w obiekcie

	GLuint VAO[MAX_VAO]; // id tablic wierzcholkow obiektow
	GLuint VBO[2 * MAX_VAO]; // id buforow wierzcholkow

	GLenum Primitives[MAX_VAO]; // tablica rodzajow prymitywow skojarzonych z VAO
	int lCoords[MAX_VAO]; // liczba wspolrzednych skojarzona z kazda VAO

	float *Coords; // tablica ze wspolrzednymi
	float *Cols; // tablica z kolorami

	// ustawienia aktualnego koloru
	float col_r;
	float col_g;
	float col_b;


	// komunikaty diagnostyczne
	char _msg[1024];

};

#endif

