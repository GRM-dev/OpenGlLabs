using SenryakuShuriken.Core.ObjectData;

namespace SenryakuShuriken.Core.MapGen
{
    public class Map:GlObject
    {
        private readonly int _id;
        private readonly string _name;
        private readonly MapField[] _mapFields;
        private int[] _bounds;

        public Map(int id, string name, MapField[] mapFields, int[] bounds)
        {
            _id = id;
            _name = name;
            _mapFields = mapFields;
            _bounds = bounds;
        }
    }
}
