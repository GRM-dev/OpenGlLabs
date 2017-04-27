namespace SenryakuShuriken.Core.MapGen
{
    public class MapField
    {
        private readonly int _x;
        private readonly int _y;
        private readonly MapFieldType _fieldType;
        private readonly Orientation _orientation;
        private readonly bool _start;
        private readonly bool _end;

        public MapField(int x, int y, MapFieldType fieldType = MapFieldType.EMPTY, bool start = false, bool end = false, Orientation orientation = Orientation.NORTH)
        {
            _x = x;
            _y = y;
            _fieldType = fieldType;
            _orientation = orientation;
            _start = start;
            _end = end;
        }
    }
}
