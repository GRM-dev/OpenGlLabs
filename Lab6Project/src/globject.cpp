#include "globject.h"

int CalcNormal(float A[], float B[], float C[], float *N)
{
	const int x = 0;
	const int y = 1;
	const int z = 2;
	float U[3];
	float V[3];
	// oblicz wspolrzedne wektorow U oraz V
	U[x] = A[x] - B[x];
	U[y] = A[y] - B[y];
	U[z] = A[z] - B[z];
	V[x] = B[x] - C[x];
	V[y] = B[y] - C[y];
	V[z] = B[z] - C[z];
	// wyznacz wspolrzedne normalnej
	N[x] = U[y] * V[z] - U[z] * V[y];
	N[y] = U[z] * V[x] - U[x] * V[z];
	N[z] = U[x] * V[y] - U[y] * V[x];
	return 1;
}

int Normalize(float *N)
{
	const int x = 0;
	const int y = 1;
	const int z = 2;
	// oblicz dlugosc wektora
	float L = static_cast<float>(sqrt(N[x] * N[x] + N[y] * N[y] + N[z] * N[z]));
	if (L < 0.01) L = 0.01;
	// wyznacz wspolrzedne normalnej
	N[x] /= L;
	N[y] /= L;
	N[z] /= L;
	return 1;
}
//--------------------------------------------------------------------------------------------
// domyslny konstruktor
glObject::glObject()
{
	Coords = static_cast<float *>(malloc(sizeof(float)));
	Cols = static_cast<float *>(malloc(sizeof(float)));
	Normals = static_cast<float *>(malloc(sizeof(float)));
	TexCoords = static_cast<float *>(malloc(sizeof(float)));
	nx = 1.0;
	ny = 0.0;
	nz = 0.0;
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
	if (Normals) free(Normals);
	if (TexCoords) free(TexCoords);
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
// ustawia aktualna normalna
void glObject::SetNormal(float _nx, float _ny, float _nz)
{
	nx = _nx;
	ny = _ny;
	nz = _nz;
}
//--------------------------------------------------------------------------------------------
// usuwa alokowane atrybuty
void glObject::CleanUp()
{
	lVAO = 0;
}
//--------------------------------------------------------------------------------------------
// rozpoczyna tworzenie tablicy VAO dla danego prymitywu
void glObject::BeginObject(GLenum P, GLuint TextureId)
{

	lVAO++;
	// przypisz rodzaj prymitywu do narysowania VAO
	Primitives[lVAO - 1] = P;

	// przypisz Id tekstury do narysowania VAO
	Textures[lVAO - 1] = TextureId;

	// wyzeruj licznik wspolrzednych
	lCoords[lVAO - 1] = 0;
	Coords = static_cast<float *>(malloc(sizeof(float)));
	Cols = static_cast<float *>(malloc(sizeof(float)));
	Normals = static_cast<float *>(malloc(sizeof(float)));

	if (lVAO > MAX_VAO) ThrowException("Przekroczono maksymalna liczbe VAO w glObject");

	GLuint VAO_id[1];
	// przygotuj tablice VAO
	glGenVertexArrays(1, VAO_id);
	VAO[lVAO - 1] = VAO_id[0];

	glBindVertexArray(VAO[lVAO - 1]);

	GLuint VBO_id[4];
	// przygotuj bufory VBO
	glGenBuffers(4, VBO_id);

	VBO[4 * lVAO - 4] = VBO_id[0];
	VBO[4 * lVAO - 3] = VBO_id[1];
	VBO[4 * lVAO - 2] = VBO_id[2];
	VBO[4 * lVAO - 1] = VBO_id[3];

}
//--------------------------------------------------------------------------------------------
// dodaje wierzcholek do listy ze wsp. tekstury
void glObject::AddVertex(float x, float y, float z, float u, float v)
{
	lCoords[lVAO - 1] += 3;
	Coords = static_cast<float *>(realloc(Coords, lCoords[lVAO - 1] * sizeof(float)));
	if (Coords == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Coords[lCoords[lVAO - 1] - 3] = x;
	Coords[lCoords[lVAO - 1] - 2] = y;
	Coords[lCoords[lVAO - 1] - 1] = z;

	Cols = static_cast<float *>(realloc(Cols, lCoords[lVAO - 1] * sizeof(float)));
	if (Cols == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Cols[lCoords[lVAO - 1] - 3] = col_r;
	Cols[lCoords[lVAO - 1] - 2] = col_g;
	Cols[lCoords[lVAO - 1] - 1] = col_b;

	Normals = static_cast<float *>(realloc(Normals, lCoords[lVAO - 1] * sizeof(float)));
	if (Normals == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	Normals[lCoords[lVAO - 1] - 3] = nx;
	Normals[lCoords[lVAO - 1] - 2] = ny;
	Normals[lCoords[lVAO - 1] - 1] = nz;

	TexCoords = static_cast<float *>(realloc(TexCoords, lCoords[lVAO - 1] * sizeof(float)));
	if (TexCoords == nullptr) ThrowException("glObject:: Blad realokacji pamieci");
	TexCoords[lCoords[lVAO - 1] - 3] = u;
	TexCoords[lCoords[lVAO - 1] - 2] = v;
	TexCoords[lCoords[lVAO - 1] - 1] = 0.0;
}
//--------------------------------------------------------------------------------------------
void glObject::EndObject()
{
	// podlacz pierwszy obiekt z VAOs
	glBindVertexArray(VAO[lVAO - 1]);
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[4 * lVAO - 4]);
	// wypelnij bufor wspolrzednymi wierzcholka

	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Coords, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 0 (wskazany w shaderze)
	glEnableVertexAttribArray(0);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	// podlacz drugi bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[4 * lVAO - 3]);
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Cols, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 1 (wskazany w shaderze)
	glEnableVertexAttribArray(1);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	// podlacz trzeci bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[4 * lVAO - 2]);
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), Normals, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 2 (wskazany w shaderze)
	glEnableVertexAttribArray(2);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(2, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	// podlacz czwarty bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[4 * lVAO - 1]);
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO - 1] * sizeof(float), TexCoords, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 3 (wskazany w shaderze)
	glEnableVertexAttribArray(3);
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(3, 3, GL_FLOAT, GL_FALSE, 0, nullptr);

	glBindVertexArray(0);
}
//--------------------------------------------------------------------------------------------
void glObject::Draw()
{
	for (int i = 0; i < lVAO; i++)
	{
		if (Textures[i] == 0)
		{
			glDisable(GL_TEXTURE_2D);
		}
		else
		{
			glBindTexture(GL_TEXTURE_2D, Textures[i]);
			glEnable(GL_TEXTURE_2D);
		}
		glBindVertexArray(VAO[i]);
		glDrawArrays(Primitives[i], 0, lCoords[i] / 3);
		glBindVertexArray(0);
	}
}
//--------------------------------------------------------------------------------------------
// the end