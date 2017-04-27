using System.Collections.Generic;
using SenryakuShuriken.Core.ObjectData;

namespace SenryakuShuriken.Graphic.Renderers
{
    internal class BallRenderer : BasicRenderer
    {

        protected internal BallRenderer()
        {
        }

        protected override void Draw(GlObject o)
        {
        }

        public override IEnumerable<GlObject> ObjectsToRender()
        {
            yield break;
        }
    }
}