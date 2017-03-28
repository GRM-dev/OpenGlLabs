using System.Collections.Generic;
using OpenGlProject.Core;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Core.Objects;

namespace OpenGlProject.Graphic.Renderers
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