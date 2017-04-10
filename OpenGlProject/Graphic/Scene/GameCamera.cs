using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using SharpGL.SceneGraph.Cameras;

namespace OpenGlProject.Graphic.Scene
{
    public class GameCamera
    {
        private double[] _matrix;

        public GameCamera()
        {
            Cam=new PerspectiveCamera();
        }

        public PerspectiveCamera Cam { get; }
        public double[] Matrix => _matrix;
    }
}
