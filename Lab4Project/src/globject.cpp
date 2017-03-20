#include "globject.h"

//--------------------------------------------------------------------------------------------
// domyslny konstruktor 
glObject::glObject()
{			
	Coords = (float *) malloc(sizeof(float));	
	Cols = (float *) malloc(sizeof(float));
	Normals = (float *) malloc(sizeof(float));
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
// przygotowuje geometrie elipsoidy
void glObject::MakeEgg(GLfloat a,GLfloat b,GLfloat slices, GLfloat stacks)
{
  GLfloat x;
  
  GLfloat r1 = 0.0; // promienie wodzace do rozpinania czworokatow
  GLfloat r2 = 0.0;
  
  GLfloat phi1; // katy dla rozpinania czworokatow
  GLfloat phi2;

  GLfloat slice_delta = 2*a / float(slices);   // przyrost odleglosi okregow na osi OX na iteracje
  GLfloat stack_delta = 360.0 / float(stacks); // przyrost wartosci kata w okregu na iteracje

  x = -a; // zacznij rysowac obiekt od x=-a
  
  BeginObject(GL_TRIANGLES);
  
  // wierzcholki fasety 
  float v1[3];
  float v2[3];
  float v3[3];
  float v4[3];

  // normalna
  float N[3];
  
  while (x <= a) // iteruj wzdluz OX az nie uzyskasz x=a
  {
	  	  
	  if (x+slice_delta < a) // wyznacz r1 z przeksztalconego wzoru na elipse lub przypisz r2=0 dla x=a 	  
	    r2 = (sqrt((a*a - (x+slice_delta)*(x+slice_delta)))*b)/a;
	  else
	    r2=0.0;

      phi1 = 0.0; // zeruj poczatkowa wartosc kata dla tworzenia okregu 

  
	  while(phi1 <= 360.0) // iteruj tworzenie okregu 
	   {
		   phi2 = phi1 + stack_delta; // oblicz phi2 wzgledem phi1
		   			   
		   v1[0] = x; v1[1] = r1*cos(phi1*3.14/180); v1[2] = r1*sin(phi1*3.14/180);
		   v2[0] = x; v2[1] = r1*cos(phi2*3.14/180); v2[2] = r1*sin(phi2*3.14/180);
		   v3[0] = x+slice_delta; v3[1] = r2*cos(phi2*3.14/180); v3[2] = r2*sin(phi2*3.14/180);
		   v4[0] = x+slice_delta; v4[1] = r2*cos(phi1*3.14/180); v4[2] = r2*sin(phi1*3.14/180);

		   // pierwszy trojkat
		   
		   AddVertex(v1[0],v1[1],v1[2]);
		   AddVertex(v2[0],v2[1],v2[2]);
		   AddVertex(v3[0],v3[1],v3[2]);
		   
		   //drugi trojkat		   
		   AddVertex(v1[0],v1[1],v1[2]);
		   AddVertex(v3[0],v3[1],v3[2]);
		   AddVertex(v4[0],v4[1],v4[2]);

		 phi1 += stack_delta; // zwieksz wartosc kata phi1
	   }
		    
	  x += slice_delta; // zwieksz odleglosc okregu
	  r1 = (sqrt((a*a - x*x))*b)/a; // wyznacz r1 z przeksztalconego wzoru na elipse
  }
  EndObject();
}
//--------------------------------------------------------------------------------------------
// rozpoczyna tworzenie tablicy VAO dla danego prymitywu
void glObject::BeginObject(GLenum P)
{	
	
	lVAO++;
	// przypisz rodzaj prymitywu do narysowania VAO
	Primitives[lVAO-1] = P;

	// wyzeruj licznik wspolrzednych 
	lCoords[lVAO-1] = 0;
	Coords = (float *) malloc(sizeof(float));
	Cols = (float *) malloc(sizeof(float));
	Normals = (float *) malloc(sizeof(float));

	if (lVAO > MAX_VAO) ThrowException("Przekroczono maksymalna liczbe VAO w glObject");
	
	GLuint VAO_id[1];	
	// przygotuj tablice VAO
	glGenVertexArrays(1, VAO_id); 
	VAO[lVAO-1] = VAO_id[0];

	glBindVertexArray(VAO[lVAO-1]);

	GLuint VBO_id[3];	
	// przygotuj bufory VBO
	glGenBuffers(3, VBO_id);
	
	VBO[3*lVAO-3] = VBO_id[0];
	VBO[3*lVAO-2] = VBO_id[1];
	VBO[3*lVAO-1] = VBO_id[2];
	
}
//--------------------------------------------------------------------------------------------
// dodaje wierzcholek do listy
void glObject::AddVertex(float x, float y, float z)
{
	lCoords[lVAO-1] +=3;
	Coords = (float *) realloc(Coords, lCoords[lVAO-1]*sizeof(float));
	if (Coords == NULL) ThrowException("glObject:: Blad realokacji pamieci");
	Coords[lCoords[lVAO-1]-3] = x;
	Coords[lCoords[lVAO-1]-2] = y;
	Coords[lCoords[lVAO-1]-1] = z;

	Cols = (float *) realloc(Cols, lCoords[lVAO-1]*sizeof(float));
	if (Cols == NULL) ThrowException("glObject:: Blad realokacji pamieci");
	Cols[lCoords[lVAO-1]-3] = col_r;
	Cols[lCoords[lVAO-1]-2] = col_g;
	Cols[lCoords[lVAO-1]-1] = col_b;	

	Normals = (float *) realloc(Normals, lCoords[lVAO-1]*sizeof(float));
	if (Normals == NULL) ThrowException("glObject:: Blad realokacji pamieci");
	Normals[lCoords[lVAO-1]-3] = nx;
	Normals[lCoords[lVAO-1]-2] = ny;
	Normals[lCoords[lVAO-1]-1] = nz;	
}
//--------------------------------------------------------------------------------------------
void glObject::EndObject()
{
	// podlacz pierwszy obiekt z VAOs
	glBindVertexArray(VAO[lVAO-1]);
	// podlacz pierwszy bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3*lVAO-3]);	
	// wypelnij bufor wspolrzednymi wierzcholka 

	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO-1]*sizeof(float), Coords, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 0 (wskazany w shaderze)
	glEnableVertexAttribArray(0); 
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 0, 0);	

	// podlacz drugi bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3*lVAO-2]);	
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO-1]*sizeof(float), Cols, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 1 (wskazany w shaderze)
	glEnableVertexAttribArray(1); 
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 0, 0);	

	// podlacz trzeci bufor VBOs
	glBindBuffer(GL_ARRAY_BUFFER, VBO[3*lVAO-1]);	
	// wypelnij bufor kolorami wierzcholka
	glBufferData(GL_ARRAY_BUFFER, lCoords[lVAO-1]*sizeof(float), Normals, GL_STATIC_DRAW);
	// wybierz atrybut indeksie 2 (wskazany w shaderze)
	glEnableVertexAttribArray(2); 
	// powiaz dane z bufora ze wskazanym atrybutem
	glVertexAttribPointer(2, 3, GL_FLOAT, GL_FALSE, 0, 0);	

	glBindVertexArray(0);
}
//--------------------------------------------------------------------------------------------
void glObject::Draw()
{
	for (int i = 0; i < lVAO; i++)
	{
		glBindVertexArray(VAO[i]);
		glDrawArrays(Primitives[i],0,lCoords[i]/3);		
		glBindVertexArray(0);
	}	
}


//--------------------------------------------------------------------------------------------
// the end
