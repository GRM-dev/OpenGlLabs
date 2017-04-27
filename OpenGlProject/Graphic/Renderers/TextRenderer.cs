using System.Collections.Generic;
using SenryakuShuriken.Core.ObjectData;
using SenryakuShuriken.Core.Objects;

namespace SenryakuShuriken.Graphic.Renderers
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
