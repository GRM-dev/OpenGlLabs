using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Graphic.Renderers;

namespace OpenGlProject.Core.Objects
{
    public class Cube : GlObject
    {
        public Cube(float a)
        {
            A = a;
        }

        public float A { get; set; }

    }
}
