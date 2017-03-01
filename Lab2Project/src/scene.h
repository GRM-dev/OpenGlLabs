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

//----------------------------------- DEFINES -------------------------------------------------
#define ESCAPE 27

//--------------------------------------------------------------------------------------------
// 								CLASS INTERFACE
//--------------------------------------------------------------------------------------------
class Scene
{

public:

Scene(int new_width,int new_height); //  domyslny konstruktor
~Scene(); // domyslny destruktor 

void Draw(); // rysuje zawartosc sceny

void Resize(int new_width, int new_height); // zmienia rozmiar sceny 

void Init(); // procedura inicjujaca przetwarzanie

void KeyPressed(unsigned char key, int x, int y); // obsluga klawiatury

GLuint LoadShader(GLenum type,const char *file_name); // laduje program shadera z zewnetrznego pliku 

private:


void PreparePrograms(); // przygotowuje programy przetwarzania 
void PrepareObjects(); // przygotowuje obiekty do wyswietlenia 

void Polygon(int n, float r,GLuint VOA, GLuint VBO); // rysuje wielokat foremny 
void Epicykl(int n, float r, float R, GLuint VAO, GLuint VBO);

// rozmiary sceny 
int width;
int height;

// zasoby programu przetwarzania
GLuint program;
GLuint vertex_shader;
GLuint fragment_shader;


// zasoby obiektow 
#define VAO_cnt 1 // liczba tablic wierzcholkow obiektow (= liczba obiektow)  
#define VBO_cnt 1 // liczba buforow skojarzonych z VAOs

GLuint VAOs[VAO_cnt]; // id tablic wierzcholkow obiektow 
GLuint VBOs[VBO_cnt]; // id tablic buforów 


// komunikaty diagnostyczne 
char _msg[1024];

};

#endif
//------------------------------- KONIEC PLIKU -----------------------------------------------
