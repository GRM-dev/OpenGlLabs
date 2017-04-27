using SharpGL;
using SharpGL.VertexBuffers;

namespace SenryakuShuriken.Graphic.Scene
{
    public static class GlObjectFactory
    {
        /// <summary>
        /// Creates Vertex Buffer Array (VBA)
        /// </summary>
        /// <param name="gl">OpenGl instance</param>
        /// <returns>VertexBufferArray</returns>
        public static VertexBufferArray CreateVBO(OpenGL gl)
        {
            var vbo = new VertexBufferArray();
            vbo.Create(gl);
            return vbo;
        }

        /// <summary>
        /// Creates Vertex Buffer (VB)
        /// </summary>
        /// <param name="gl">OpenGl instance</param>
        /// <param name="attributeIndex">index of attribute to create VB for</param>
        /// <param name="vertices">array of vertices</param>
        /// <param name="stride">stride</param>
        /// <param name="isNormalised"></param>
        /// <returns>VertexBuffer</returns>
        public static VertexBuffer CreateVertexBuffer(OpenGL gl, uint attributeIndex, float[] vertices, int stride = 3, bool isNormalised = false)
        {
            var vertexBuffer = new VertexBuffer();
            vertexBuffer.Create(gl);
            vertexBuffer.Bind(gl);
            vertexBuffer.SetData(gl, attributeIndex, vertices, isNormalised, stride);
            return vertexBuffer;
        }

        /// <summary>
        /// Creates IndexBuffer (IB)
        /// </summary>
        /// <param name="gl">OpenGl instance</param>
        /// <param name="indices">array of indices</param>
        /// <returns>IndexBuffer</returns>
        public static IndexBuffer CreateIndexBuffer(OpenGL gl, ushort[] indices)
        {
            var indexBuffer = new IndexBuffer();
            indexBuffer.Create(gl);
            indexBuffer.Bind(gl);
            indexBuffer.SetData(gl, indices);
            return indexBuffer;
        }
    }
}
