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
#include "skybox.h"
#include "texture.h"

#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "sphere.h"

//----------------------------------- DEFINES -------------------------------------------------
#define ESCAPE 27

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

	void Animate(); // przeprowadza animacje sceny

	void KeyPressed(unsigned char key, int x, int y); // obsluga klawiatury

	GLuint LoadShader(GLenum type, const char *file_name); // laduje program shadera z zewnetrznego pliku

private:

	float rot_x; //  obrot obiektu wzgledem X
	float rot_y; //  obrot obiektu wzgledem Y

	float Cam_angle; // kat obrotu kamery
	float Cam_r; // promien kamery

	float LightAmbient;

	glObject *Cube;
	glObject *Axes;

	glTexture *Face;

	glSkyBox *SkyBox;
	glSphere* Moon;

	void PreparePrograms(); // przygotowuje programy przetwarzania
	void PrepareObjects(); // przygotowuje obiekty do wyswietlenia

	// rozmiary sceny
	int width;
	int height;

	glm::mat4 mProjection;
	glm::mat4 mModelView;

	// zasoby programu przetwarzania
	GLuint program;
	GLuint vertex_shader;
	GLuint fragment_shader;

	// komunikaty diagnostyczne
	char _msg[1024];
	int err;
};

#endif
//------------------------------- KONIEC PLIKU -----------------------------------------------
