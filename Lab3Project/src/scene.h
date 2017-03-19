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
#include "globject.h"

#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"

//----------------------------------- DEFINES -------------------------------------------------

#define K_UP 0
#define K_DOWN 1
#define K_LEFT 2
#define K_RIGHT 3

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
	void RunLogic();

	bool IsLogicRunning();
	void KeyPressed(unsigned char key, int x, int y); // obsluga klawiatury
	void KeyUnPressed(unsigned char key, int x, int y);

	GLuint LoadShader(GLenum type, const char *file_name); // laduje program shadera z zewnetrznego pliku

	bool KEYS[4];
	bool LogicRunning;
	GLint iModelViewLoc;
	GLint iProjectionLoc;

private:
	float drx = 0.03f;
	float dry = 0.7f;
	float rot_x; //  obrot obiektu wzgledem X
	float rot_y; //  obrot obiektu wzgledem Y

	float dx = 0.1f;
	float dy = 0.1f;
	float dz = 0.1f;
	float pos_x;
	float pos_y;
	float pos_z;

	bool turbo;

	glObject *Axes;
	glObject *Cube;
	glObject *Plane;

	glm::vec3 *up_vec;
	glm::mat4 mProjection;
	glm::mat4 mModelView;

	void PreparePrograms(); // przygotowuje programy przetwarzania
	void PrepareObjects(); // przygotowuje obiekty do wyswietlenia

	// rozmiary sceny
	int width;
	int height;

	// zasoby programu przetwarzania
	GLuint program;
	GLuint vertex_shader;
	GLuint fragment_shader;

	// komunikaty diagnostyczne
	char _msg[1024];
	volatile bool lock;

};

#endif
//------------------------------- KONIEC PLIKU -----------------------------------------------
