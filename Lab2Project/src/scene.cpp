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

//--------------------------------------------------------------------------------------------
// zglasza wyjatek z komunikatem do debuggowania

//--------------------------------------------------------------------------------------------
Scene::Scene(int new_width, int new_height)
{
	width = new_width;
	height = new_height;
	dX = 0;
	dY = 0;
}
//--------------------------------------------------------------------------------------------
// Domyslny destruktor
Scene::~Scene()
{
	// usun program przetwarzania
	if (glIsProgram(program)) glDeleteProgram(program);

	// usun bufory
	glDeleteBuffers(VBO_cnt, VBOs);

	// usub tablice atrybutow wierzcholkow
	glDeleteVertexArrays(VAO_cnt, VAOs);
}
//--------------------------------------------------------------------------------------------
// przygotowuje programy cienionwania
void Scene::PreparePrograms()
{
	program = glCreateProgram();
	if (!glIsProgram(program)) ThrowException("Nie udalo sie utworzyc programu");

	vertex_shader = LoadShader(GL_VERTEX_SHADER, "../Shaders/vs_2.glsl");
	glAttachShader(program, vertex_shader);

	fragment_shader = LoadShader(GL_FRAGMENT_SHADER, "../Shaders/fs_2.glsl");
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
		glGetProgramInfoLog(program, logLength, NULL, log);
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
		glGetProgramInfoLog(program, logLength, NULL, log);
		PrintLog(log);
		delete[] log;
		ThrowException("Blad walidacji programu");
	}
	else
		PrintLog("Program prawidlowy");

	glUseProgram(program);
}
//--------------------------------------------------------------------------------------------
// przygotowuje obiekty do wyswietlenia
void Scene::PrepareObjects()
{
	// przygotuj tablice VAO
	glGenVertexArrays(VAO_cnt, VAOs);

	// przygotuj bufory VBO
	glGenBuffers(VBO_cnt, VBOs);

	float pos_tab[12]; // tablica 12 wspolrzednych (4 wierzcholki)

	pos_tab[0] = -0.5f; pos_tab[1] = 0.5f;   pos_tab[2] = 0.0f;
	pos_tab[3] = 0.5f;  pos_tab[4] = 0.5f;   pos_tab[5] = 0.0f;
	pos_tab[6] = -0.5f; pos_tab[7] = -0.5f;  pos_tab[8] = 0.0f;
	pos_tab[9] = 0.5f;  pos_tab[10] = -0.5f; pos_tab[11] = 0.0f;

	// podlacz pierwszy obiekt z VAOs
	glBindVertexArray(VAOs[0]);
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBOs[0]);
	// wypelnij bufor wspolrzednymi wierzcholka
	glBufferData(GL_ARRAY_BUFFER, sizeof(pos_tab), pos_tab, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 0 (wskazany w shaderze)
	glEnableVertexAttribArray(0);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, 0);

	float col_tab[12]; // tablica kolorow (4 wierzcholki)
	// wypelnij kolorami (r,g,b)
	col_tab[0] = 1.0f; 	col_tab[1] = 0.0f; 	col_tab[2] = 0.0f;  // czerwony
	col_tab[3] = 0.0f; 	col_tab[4] = 1.0f; 	col_tab[5] = 0.0f;	// zielony
	col_tab[6] = 0.0f; col_tab[7] = 0.0f; col_tab[8] = 1.0f;	// niebieski
	col_tab[9] = 1.0f; col_tab[10] = 1.0f; col_tab[11] = 0.0f;	// zolty
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBOs[1]);
	// wypelnij bufor wspolrzednymi wierzcholka
	glBufferData(GL_ARRAY_BUFFER, sizeof(col_tab), col_tab, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 1 (wskazany w shaderze)
	glEnableVertexAttribArray(1);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, 0);

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
}
//--------------------------------------------------------------------------------------------
// laduje program shadera z zewnetrznego pliku
GLuint Scene::LoadShader(GLenum type, const char *file_name)
{
	// zmienna plikowa
	FILE *fil = NULL;
	// sproboj otworzyc plik
	fil = fopen(file_name, "rb");
	// sprawdz, czy plik sie otworzyl
	sprintf(_msg, "Nie mozna otworzyc %s", file_name);
	if (fil == NULL)  ThrowException(_msg);

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
	glShaderSource(shader, 1, const_cast<const GLchar**>(&srcBuf), NULL);

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
		glGetShaderInfoLog(shader, logLength, NULL, log);
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
}
//--------------------------------------------------------------------------------------------
// kontrola naciskania klawiszy klawiatury
void Scene::KeyPressed(unsigned char key, int x, int y)
{
	if (key == ESCAPE) ThrowException("Zatrzymaj program");
	if (key == UP_KEY) dY += 0.1;
	if (key == DOWN_KEY) dY -= 0.1;
	if (key == LEFT_KEY) dX += 0.1;
	if (key == RIGHT_KEY) dX -= 0.1;

	// pobierz polozenie zmiennej ze shadera pod dX_loc
	GLint dX_loc = glGetUniformLocation(program, "dX");
	// podstaw wartsc pod dX_loc (spowoduje nadpisanie zmiennej w shaderze)
	glUniform1f(dX_loc, dX);
	GLint dY_loc = glGetUniformLocation(program, "dY");
	glUniform1f(dY_loc, dY);


	sprintf(_msg, "%d", key);
	PrintLog(_msg);
}
//--------------------------------------------------------------------------------------------
// rysuje scene OpenGL
void Scene::Draw()
{

	// czyscimy bufor kolorow
	glClear(GL_COLOR_BUFFER_BIT);
	//wybierz obiekt identyfikowany przez VAO
	glBindVertexArray(VAOs[0]);
	//narysuj go
	glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
}
//------------------------------- KONIEC PLIKU -----------------------------------------------