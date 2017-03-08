#include "globject.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glObject::glObject()
{
	Coords = (float *)malloc(sizeof(float));
	Cols = (float *)malloc(sizeof(float));
	col_r = 0.0;
	col_g = 0.0;
	col_b = 0.0;
	lVAO = 0;
}
//--------------------------------------------------------------------------------------------
// domyslny destruktor
glObject::~glObject()
{
	if (Coords) free(Coords);
	if (Cols) free(Cols);
}
//--------------------------------------------------------------------------------------------
// ustawia aktualny kolor rysowania prymitywu
void glObject::SetColor(float r, float g, float b)
{
	col_r = r;
	col_g = g;
	col_b = b;
}
//--------------------------------------------------------------------------------------------
void glObject::BeginObject(GLenum P)
{

	lVAO++;
	// przypisz rodzaj prymitywu do narysowania VAO
	Primitives[lVAO - 1] = P;

	// wyzeruj licznik wspolrzednych
	lCoords[lVAO - 1] = 0;
	Coords = (float *)malloc(sizeof(float));
	Cols = (float *)malloc(sizeof(float));

	if (lVAO > MAX_VAO) ThrowException("Przekroczono maksymalna liczbe VAO w glObject");

	GLuint VAO_id[1];
	// przygotuj tablice VAO
	glGenVertexArrays(1, VAO_id);
	VAO[lVAO - 1] = VAO_id[0];

	glBindVertexArray(VAO[lVAO - 1]);

	GLuint VBO_id[2];
	// przygotuj bufory VBO
	glGenBuffers(2, VBO_id);

	VBO[2 * lVAO - 2] = VBO_id[0];
	VBO[2 * lVAO - 1] = VBO_id[1];
}
//--------------------------------------------------------------------------------------------
void glObject::AddVertex(float x, float y, float z)
{
	lCoords[lVAO - 1] += 3;
	Coords = (float *)realloc(Coords, lCoords[lVAO - 1] * sizeof(float));
	if (Coords == NULL) ThrowException("glObject:: Blad realokacji pamieci");
	Coords[lCoords[lVAO - 1] - 3] = x;
	Coords[lCoords[lVAO - 1] - 2] = y;
	Coords[lCoords[lVAO - 1] - 1] = z;

	Cols = (float *)realloc(Cols, lCoords[lVAO - 1] * sizeof(float));
	if (Cols == NULL) ThrowException("glObject:: Blad realokacji pamieci");
	Cols[lCoords[lVAO - 1] - 3] = col_r;
	Cols[lCoords[lVAO - 1] - 2] = col_g;
	Cols[lCoords[lVAO - 1] - 1] = col_b;
}
//--------------------------------------------------------------------------------------------
void glObject::EndObject()
{
	// podlacz pierwszy obiekt z VAOs
	glBindVertexArray(VAO[lVAO - 1]);
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[2 * lVAO - 2]);
	// wypelnij bufor wspolrzednymi wierzcholka

	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Coords, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 0 (wskazany w shaderze)
	glEnableVertexAttribArray(0);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, 0);

	// podlacz drugi bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[2 * lVAO - 1]);
	// wypelnij bufor wspolrzednymi wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Cols, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 1 (wskazany w shaderze)
	glEnableVertexAttribArray(1);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, 0);
	glBindVertexArray(0);
}
//--------------------------------------------------------------------------------------------
void glObject::Draw()
{
	for (int i = 0; i < lVAO; i++)
	{
		glBindVertexArray(VAO[i]);
		glDrawArrays(Primitives[i], 0, lCoords[i] / 3);
		glBindVertexArray(0);
	}
}

//--------------------------------------------------------------------------------------------
// the end
