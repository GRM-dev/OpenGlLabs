using System.Collections.Generic;
using OpenGlProject.Core.ObjectData;
using OpenGlProject.Core.Objects;

namespace OpenGlProject.Graphic.Renderers
{
    public class TextRenderer : BasicRenderer
    {
        protected internal TextRenderer()
        {
        }


        protected override void Draw(GlObject o)
        {
        }

        public override IEnumerable<GlObject> ObjectsToRender()
        {
            return Texts;
        }

        private IEnumerable<Text> Texts => _objects.ConvertAll(o => o as Text);
    }
}
