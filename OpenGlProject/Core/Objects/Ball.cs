using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core.Object;
using OpenGlProject.Graphic.Renderers;

namespace OpenGlProject.Core.Objects
{
    public class Ball : GlObject
    {
        public Ball(float r)
        {
            R = r;
        }

        public float R { get; set; }
    }
}
