//--------------------------------------------------------------------------------------------
//
// File: 	scene.cpp
// Author:	P. Katarzynski (CCE)
//
// Description: Implementacja klasy sceny OpenGL
//
//--------------------------------------------------------------------------------------------
//									ZALEZNOSI
//--------------------------------------------------------------------------------------------

#include "scene.h"
#include <chrono>
#include <thread>

//--------------------------------------------------------------------------------------------
// zglasza wyjatek z komunikatem do debuggowania

//--------------------------------------------------------------------------------------------
Scene::Scene(int new_width, int new_height)
{
	width = new_width;
	height = new_height;
	turbo = false;
	rot_x = 0.0;
	rot_y = 0.0;
	pos_x = 0.0;
	pos_y = 0.0;
	pos_z = 0.0;
	Axes = nullptr;
	Cube = nullptr;
	Plane = nullptr;
	up_vec = new glm::vec3(0.0f, 1.0f, 0.0f);
	KEYS[K_UP] = false;
	KEYS[K_DOWN] = false;
	KEYS[K_LEFT] = false;
	KEYS[K_RIGHT] = false;
	/*KeyPressed(VK_UP, 0, 0);
	KeyUnPressed(VK_UP, 0, 0);*/
}
//--------------------------------------------------------------------------------------------
// Domyslny destruktor
Scene::~Scene()
{
	// usun program przetwarzania
	if (glIsProgram(program)) glDeleteProgram(program);
	if (Axes) delete Axes;
	if (Cube) delete Cube;
	if (Plane) delete Plane;
	if (up_vec) delete up_vec;
}
//--------------------------------------------------------------------------------------------
// przygotowuje programy cienionwania
void Scene::PreparePrograms()
{
	program = glCreateProgram();
	if (!glIsProgram(program)) ThrowException("Nie udalo sie utworzyc programu");

	vertex_shader = LoadShader(GL_VERTEX_SHADER, "../Shaders/vs_3.glsl");
	glAttachShader(program, vertex_shader);

	fragment_shader = LoadShader(GL_FRAGMENT_SHADER, "../Shaders/fs_3.glsl");
	glAttachShader(program, fragment_shader);

	// linkowanie programu
	glLinkProgram(program);

	GLint link_status;
	glGetProgramiv(program, GL_LINK_STATUS, &link_status);
	if (link_status == GL_FALSE)
	{
		// pobranie i wyœwietlenie komunikatu b³êdu
		GLint logLength;
		glGetProgramiv(program, GL_INFO_LOG_LENGTH, &logLength);
		char *log = new char[logLength];
		glGetProgramInfoLog(program, logLength, nullptr, log);
		PrintLog(log);
		delete[] log;
		ThrowException("Blad linkowania programu");
	}
	else
		PrintLog("Program zlinkowany");

	// walidowanie programu
	glValidateProgram(program);
	GLint validate_status;
	// sprawdzenie poprawnoœci walidacji obiektu programu
	glGetProgramiv(program, GL_VALIDATE_STATUS, &validate_status);
	if (validate_status == GL_FALSE)
	{
		// pobranie i wyœwietlenie komunikatu b³êdu
		GLint logLength;
		glGetProgramiv(program, GL_INFO_LOG_LENGTH, &logLength);
		char *log = new char[logLength];
		glGetProgramInfoLog(program, logLength, nullptr, log);
		PrintLog(log);
		delete[] log;
		ThrowException("Blad walidacji programu");
	}
	else
		PrintLog("Program prawidlowy");

	glUseProgram(program);
}
//--------------------------------------------------------------------------------------------
void Scene::PrepareObjects()
{
	Axes = new glObject();
	Axes->BeginObject(GL_LINES);
	Axes->SetColor(1.0, 0.0, 0.0);
	// os X w kolorze czerwonym
	Axes->AddVertex(0.0, 0.0, 0.0);
	Axes->AddVertex(10.0, 0.0, 0.0);
	// os Y w kolorze zielonym
	Axes->SetColor(0.0, 1.0, 0.0);
	Axes->AddVertex(0.0, 0.0, 0.0);
	Axes->AddVertex(0.0, 10.0, 0.0);
	// os Z w kolorze niebieskim
	Axes->SetColor(0.0, 0.0, 1.0);
	Axes->AddVertex(0.0, 0.0, 0.0);
	Axes->AddVertex(0.0, 0.0, 10.0);
	Axes->EndObject();

	Cube = new glObject();
	// sciany prostopadle do OX
	Cube->SetColor(0.5, 0.0, 0.0);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(0.5, 0.5, 0.5);
	Cube->AddVertex(0.5, -0.5, 0.5);
	Cube->AddVertex(0.5, 0.5, -0.5);
	Cube->AddVertex(0.5, -0.5, -0.5);
	Cube->EndObject();
	Cube->SetColor(0.3, 0.0, 0.0);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(-0.5, 0.5, 0.5);
	Cube->AddVertex(-0.5, -0.5, 0.5);
	Cube->AddVertex(-0.5, 0.5, -0.5);
	Cube->AddVertex(-0.5, -0.5, -0.5);
	Cube->EndObject();
	// sciany prostopadle do OY
	Cube->SetColor(0.0, 0.5, 0.0);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(0.5, 0.5, 0.5);
	Cube->AddVertex(-0.5, 0.5, 0.5);
	Cube->AddVertex(0.5, 0.5, -0.5);
	Cube->AddVertex(-0.5, 0.5, -0.5);
	Cube->EndObject();
	Cube->SetColor(0.0, 0.3, 0.0);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(-0.5, -0.5, 0.5);
	Cube->AddVertex(0.5, -0.5, 0.5);
	Cube->AddVertex(-0.5, -0.5, -0.5);
	Cube->AddVertex(0.5, -0.5, -0.5);
	Cube->EndObject();
	// sciany prostopadle do OZ
	Cube->SetColor(0.0, 0.0, 0.5);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(0.5, 0.5, 0.5);
	Cube->AddVertex(-0.5, 0.5, 0.5);
	Cube->AddVertex(0.5, -0.5, 0.5);
	Cube->AddVertex(-0.5, -0.5, 0.5);
	Cube->EndObject();
	Cube->SetColor(0.0, 0.0, 0.3);
	Cube->BeginObject(GL_TRIANGLE_STRIP);
	Cube->AddVertex(0.5, 0.5, -0.5);
	Cube->AddVertex(-0.5, 0.5, -0.5);
	Cube->AddVertex(0.5, -0.5, -0.5);
	Cube->AddVertex(-0.5, -0.5, -0.5);
	Cube->EndObject();

	Plane = new glObject();
	Plane->SetColor(0.3, 0.3, 0.3);
	Plane->BeginObject(GL_LINES);
	for (float p = -100.0; p <= 100.0; p = p + 5.0)
	{
		Plane->AddVertex(-100.0f, 0.0f, p);
		Plane->AddVertex(100.0f, 0.0f, p);
		Plane->AddVertex(p, 0.0f, -100.0);
		Plane->AddVertex(p, 0.0f, 100.0);
	} Plane->EndObject();

}
//--------------------------------------------------------------------------------------------
// Odpowiada za skalowanie sceny przy zmianach rozmiaru okna
void Scene::Resize(int new_width, int new_height)
{
	// przypisz nowe gabaryty do pol klasy
	width = new_width;
	// uwzgledniaj obecnosc kontrolki wizualnej
	height = new_height - 100;
	// rozszerz obszar renderowania do obszaru o wymiarach 'width' x 'height'
	glViewport(0, 100, width, height);

	mProjection = glm::perspective(45.0f, (GLfloat)width / (GLfloat)height, 0.1f, 100.0f);
}
//--------------------------------------------------------------------------------------------
// laduje program shadera z zewnetrznego pliku
GLuint Scene::LoadShader(GLenum type, const char *file_name)
{
	// zmienna plikowa
	FILE *fil = nullptr;
	// sproboj otworzyc plik
	fil = fopen(file_name, "rb");
	// sprawdz, czy plik sie otworzyl
	sprintf(_msg, "Nie mozna otworzyc %s", file_name);
	if (fil == nullptr)  ThrowException(_msg);

	// okresl rozmiar pliku
	fseek(fil, 0, SEEK_END);
	long int file_size = ftell(fil);
	// przewin na poczatek
	rewind(fil);
	// utworzenie bufora na kod zrodlowy programu shadera
	GLchar *srcBuf = new GLchar[(file_size + 1) * sizeof(GLchar)];

	// przeczytanie kodu shadera z pliku
	fread(srcBuf, 1, file_size, fil);

	// zamknij plik
	fclose(fil);

	// tekst programu MUSI miec NULL na koncu
	srcBuf[file_size] = 0x00;

	// utworzenie obiektu shadera
	GLuint shader = glCreateShader(type);

	// przypisanie zrodla do shadera
	glShaderSource(shader, 1, const_cast<const GLchar**>(&srcBuf), nullptr);

	// sprzatanie
	delete[] srcBuf;

	// proba skompilowania programu shadera
	glCompileShader(shader);

	// sprawdzenie czy sie udalo
	GLint compile_status;
	glGetShaderiv(shader, GL_COMPILE_STATUS, &compile_status);

	if (compile_status != GL_TRUE) // nie udalo sie
	{
		GLint logLength;
		glGetShaderiv(shader, GL_INFO_LOG_LENGTH, &logLength);
		char *log = new char[logLength];
		glGetShaderInfoLog(shader, logLength, nullptr, log);
		sprintf(_msg, "Blad kompilacji pliku shadera %s", file_name);
		PrintLog(_msg);
		PrintLog(log);
		ThrowException("Blad kompilacji shadera");
		delete[]log;
	}
	else
	{
		sprintf(_msg, "Plik shadera %s skompilowany", file_name);
		PrintLog(_msg);
	}
	return shader; // zwroc id shadera
}
//--------------------------------------------------------------------------------------------
// inicjuje proces renderowania OpenGL
void Scene::Init()
{
	// inicjalizacja modu³u glew
	GLenum err = glewInit();
	if (GLEW_OK != err)
	{
		sprintf(_msg, "GLew error: %s\n", glewGetErrorString(err));
		ThrowException(_msg);
	}

	// pobierz informacje o wersji openGL
	sprintf(_msg, "OpenGL vendor: ");
	strcat(_msg, (const char*)glGetString(GL_VENDOR));
	PrintLog(_msg);

	sprintf(_msg, "OpenGL renderer: ");
	strcat(_msg, (const char*)glGetString(GL_RENDERER));
	PrintLog(_msg);

	sprintf(_msg, "OpenGL version: ");
	strcat(_msg, (const char*)glGetString(GL_VERSION));
	PrintLog(_msg);

	//  ustaw kolor tla sceny (RGB Z=1.0)
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);

	// przygotuj programy shaderow
	PreparePrograms();

	// przygotuj obiekty do wyswietlenia
	PrepareObjects();

	glEnable(GL_DEPTH_TEST);
	glClearDepth(1.0);
	iModelViewLoc = glGetUniformLocation(program, "modelViewMatrix");
	iProjectionLoc = glGetUniformLocation(program, "projectionMatrix");
}

void Scene::RunLogic()
{
	if (KEYS[K_UP])
	{
		KEYS[K_UP] = true;
		pos_x += cos(rot_x)*(turbo ? 5 * dx : dx);
		pos_z += sin(rot_x)*(turbo ? 5 * dz : dz);
		rot_y += turbo ? 5 * dy : dy;
	}
	if (KEYS[K_DOWN])
	{
		KEYS[K_DOWN] = true;
		pos_x -= cos(rot_x)*(turbo ? 5 * dx : dx);
		pos_z -= sin(rot_x)*(turbo ? 5 * dz : dz);
		rot_y -= turbo ? 4 * dy : dy;
	}
	if (KEYS[K_LEFT])
	{
		KEYS[K_LEFT] = true;
		rot_x -= drx;
	}
	if (KEYS[K_RIGHT])
	{
		KEYS[K_RIGHT] = true;
		rot_x += drx;
	}
	pos_y = abs(sin(rot_y))*dry + 0.8;
}

//--------------------------------------------------------------------------------------------
// kontrola naciskania klawiszy klawiatury
void Scene::KeyPressed(unsigned char key, int x, int y)
{
	if (key == VK_ESCAPE) ThrowException("Zatrzymaj program");

	if (key == VK_SPACE)
	{
		turbo = !turbo;
	}
	switch (key) {
	case VK_UP:
		KEYS[K_UP] = true;
		break;
	case VK_DOWN:
		KEYS[K_DOWN] = true;
		break;
	case VK_LEFT:
		KEYS[K_LEFT] = true;
		break;
	case VK_RIGHT:
		KEYS[K_RIGHT] = true;
		break;
	}
	RunLogic();
	/*switch (key)
	{
	case VK_LEFT: {rot_y -= 5.0f; break; }
	case VK_UP: {rot_x -= 5.0f; break; }
	case VK_RIGHT: {rot_y += 5.0f; break; }
	case VK_DOWN: {rot_x += 5.0f; break; }
	}*/
}
void Scene::KeyUnPressed(unsigned char key, int x, int y)
{
	switch (key) {
	case VK_UP:
		KEYS[K_UP] = false;
		break;
	case VK_DOWN:
		KEYS[K_DOWN] = false;
		break;
	case VK_LEFT:
		KEYS[K_LEFT] = false;
		break;
	case VK_RIGHT:
		KEYS[K_RIGHT] = false;
		break;
	}
}

bool Scene::IsLogicRunning()
{
	for (int i = 0; i < sizeof(KEYS); i++)
	{
		if (KEYS[i])
		{
			return true;
		}
	}
	return false;
}

//--------------------------------------------------------------------------------------------
// rysuje scene OpenGL
void Scene::Draw()
{
	while (lock) {}
	lock = true;
	// czyscimy bufor kolorow
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glUniformMatrix4fv(iProjectionLoc, 1, GL_FALSE, glm::value_ptr(mProjection));

	glm::vec3 eye = glm::vec3(pos_x, pos_y, pos_z);
	glm::vec3 center = eye + glm::vec3(cos(rot_x), 0.0f, sin(rot_x));
	glm::mat4 mModelView = glm::lookAt(eye, center, *up_vec);
	mModelView = glm::rotate(mModelView, rot_x, glm::vec3(0.0f, 1.0f, 0.0f));
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));

	Axes->Draw();
	Plane->Draw();

	/*mModelView = glm::rotate(mModelView, rot_y, glm::vec3(0.0f, 1.0f, 0.0f));
	mModelView = glm::rotate(mModelView, rot_x, glm::vec3(1.0f, 0.0f, 0.0f));*/
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));
	Cube->Draw();

	mModelView = glm::scale(mModelView, glm::vec3(0.5, 0.5, 0.5));
	mModelView = glm::translate(mModelView, glm::vec3(0.75, 0.75, 0.75));
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));
	Cube->Draw();

	mModelView = glm::translate(mModelView, glm::vec3(-1.5, 0.0, 0.0));
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));
	Cube->Draw();

	mModelView = glm::translate(mModelView, glm::vec3(0.0, 0.0, -1.5));
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));
	Cube->Draw();

	mModelView = glm::translate(mModelView, glm::vec3(1.5, 0.0, 0.0));
	glUniformMatrix4fv(iModelViewLoc, 1, GL_FALSE, glm::value_ptr(mModelView));
	Cube->Draw();

	lock = false;
}
//------------------------------- KONIEC PLIKU -----------------------------------------------