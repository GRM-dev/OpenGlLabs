using OpenGlProject.Core.ObjectData;

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
