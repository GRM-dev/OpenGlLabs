using System.Collections.Generic;
using SenryakuShuriken.Core.ObjectData;

namespace SenryakuShuriken.Graphic.Renderers
{
    internal class BallRenderer : BasicRenderer
    {

        protected internal BallRenderer()
        {
        }

        protected override void DrawImmediate(GlObject o)
        {
        }

        protected override IEnumerable<GlObject> ObjectsToRender()
        {
            yield break;
        }
    }
}