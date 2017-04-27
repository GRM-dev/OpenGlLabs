using SenryakuShuriken.Core.ObjectData;

namespace SenryakuShuriken.Core.Objects
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
