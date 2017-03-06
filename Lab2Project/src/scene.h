//--------------------------------------------------------------------------------------------
//
// File: 	scene.hpp
// Author:	P. Katarzynski (CCE)
//
// Description: Plik naglowkowy dla klasy OpenGL
//
//--------------------------------------------------------------------------------------------

#ifndef _scene_h

#define _scene_h

#include "common.h"
#include "mrectangle.h"

//----------------------------------- DEFINES -------------------------------------------------
#define ESCAPE 27
#define UP_KEY 38
#define DOWN_KEY 40
#define LEFT_KEY 37
#define RIGHT_KEY 39
#define W_KEY 87
#define S_KEY 83
#define A_KEY 65
#define D_KEY 68

//--------------------------------------------------------------------------------------------
// 								CLASS INTERFACE
//--------------------------------------------------------------------------------------------
class Scene
{

public:

	Scene(int new_width, int new_height); //  domyslny konstruktor
	~Scene(); // domyslny destruktor

	void Draw(); // rysuje zawartosc sceny

	void Resize(int new_width, int new_height); // zmienia rozmiar sceny

	void Init(); // procedura inicjujaca przetwarzanie

	void KeyPressed(unsigned char key, int x, int y); // obsluga klawiatury

	GLuint LoadShader(GLenum type, const char *file_name); // laduje program shadera z zewnetrznego pliku

private:
	//float dX; // przesuniecie obiektu po X
	//float dY; // przesuniecie obiektu po Y
	MRectangle* r1;
	MRectangle* r2;

	void PreparePrograms(); // przygotowuje programy przetwarzania
	void PrepareObjects(); // przygotowuje obiekty do wyswietlenia

	void PrepareRect(GLuint vao, GLuint vbo_pos, GLuint vbo_col, MRectangle r);

	void Polygon(int n, float r, GLuint VOA, GLuint VBO); // rysuje wielokat foremny
	void Epicykl(int n, float r, float R, GLuint VAO, GLuint VBO);

	// rozmiary sceny
	int width;
	int height;

	// zasoby programu przetwarzania
	GLuint program;
	GLuint vertex_shader;
	GLuint fragment_shader;


	// zasoby obiektow
#define VAO_cnt 2 // liczba tablic wierzcholkow obiektow (= liczba obiektow)
#define VBO_cnt 4 // liczba buforow skojarzonych z VAOs

	GLuint VAOs[VAO_cnt]; // id tablic wierzcholkow obiektow
	GLuint VBOs[VBO_cnt]; // id tablic buforów


	// komunikaty diagnostyczne
	char _msg[1024];

};

#endif
//------------------------------- KONIEC PLIKU -----------------------------------------------
