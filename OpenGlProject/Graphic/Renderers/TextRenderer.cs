using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using OpenGlProject.Core.Objects;

namespace OpenGlProject.Graphic.Renderers
{
    public class TextRenderer:BasicRenderer
    {
        protected internal TextRenderer()
        {
        }

        public override void RenderAll()
        {
            
        }

        private IEnumerable<Text> Texts => _objects.ConvertAll(o => o as Text);
    }
}
