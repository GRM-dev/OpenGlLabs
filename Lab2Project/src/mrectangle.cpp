#include "mrectangle.h"

MRectangle::MRectangle(float x1, float y1, float size_x, float size_y)
{
	x = x1;
	y = y1;

	pos_tab[0] = -0.5f;        pos_tab[1] = size_y*0.5f;   pos_tab[2] = 0.0f;
	pos_tab[3] = size_x*0.5f;  pos_tab[4] = size_y*0.5f;   pos_tab[5] = 0.0f;
	pos_tab[6] = -0.5f;        pos_tab[7] = -0.5f;         pos_tab[8] = 0.0f;
	pos_tab[9] = size_x*0.5f;  pos_tab[10] = -0.5f;        pos_tab[11] = 0.0f;

	col_tab[0] = 1.0f; 	col_tab[1] = 0.0f; 	col_tab[2] = 0.0f;  // czerwony
	col_tab[3] = 0.0f; 	col_tab[4] = 1.0f; 	col_tab[5] = 0.0f;	// zielony
	col_tab[6] = 0.0f; col_tab[7] = 0.0f; col_tab[8] = 1.0f;	// niebieski
	col_tab[9] = 1.0f; col_tab[10] = 1.0f; col_tab[11] = 0.0f;	// zolty
}


MRectangle::~MRectangle()
{
}
