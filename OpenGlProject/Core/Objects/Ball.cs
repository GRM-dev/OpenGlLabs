using OpenGlProject.Core.ObjectData;

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
