using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using SenryakuShuriken.Core.MapGen;

namespace SenryakuShuriken.Core.Handlers
{
    public static class ManifestResourceLoader
    {
        /// <summary>
        /// Loads the named manifest resource as a text string.
        /// </summary>
        /// <param name="textFileName">Name of the text file.</param>
        /// <returns>The contents of the manifest resource.</returns>
        public static string LoadTextFile(string textFileName)
        {
            var executingAssembly = Assembly.GetExecutingAssembly();
            var pathToDots = textFileName.Replace("\\", ".");
            var location = $"{executingAssembly.GetName().Name}.{pathToDots}";

            using (var stream = executingAssembly.GetManifestResourceStream(location))
            {
                if (stream != null)
                {
                    using (var reader = new StreamReader(stream))
                    {
                        return reader.ReadToEnd();
                    }
                }
                return null;
            }
        }

        public static Map GetMap(int id)
        {
            var res = LoadTextFile(@"Resources\Maps\map_" + id + ".smap");
            if (string.IsNullOrWhiteSpace(res))
            {
                throw new FileLoadException("Empty response from File Manager!");
            }
            var lines = res.Split('\n');
            var list = new List<MapField>();
            var header = lines[0].Trim().Split('|');
            var startPosX = int.Parse(header[0]);
            var startPosY = int.Parse(header[1]);
            var endPosX = int.Parse(header[2]);
            var endPosY = int.Parse(header[3]);
            var name = header[4].Trim();
            var maxX = 0;
            var maxY = 0;
            for (var y = 1; y < lines.Length; y++)
            {
                var lineFragment = lines[y].Trim().Split(' ');
                for (var x = 0; x < lineFragment.Length; x++)
                {
                    var fs = lineFragment[x].Trim();
                    var args = fs.Split(',');
                    var start = x == startPosX && y == startPosY;
                    var end = x == endPosX && y == endPosY;
                    var f = ParseArgsToMapField(args, x, y, start, end);
                    list.Add(f);
                    maxX = x > maxX ? x : maxX;
                }
                maxY = y > maxY ? y : maxY;
            }
            var bounds = new[] { 0, 0, maxX, maxY };
            return new Map(id, name, list.ToArray(), bounds);
        }

        private static MapField ParseArgsToMapField(string[] args, int x, int y, bool start, bool end)
        {
            MapField f;
            var orient = new[] { 'n', 'w', 's', 'e' };
            if (int.TryParse(args[0].Trim(), out int tInt))
            {
                var type = (MapFieldType)tInt;
                if (args.Length > 1)
                {
                    if (orient.Contains(args[1][0]))
                    {
                        f = new MapField(x, y, type, start, end);
                    }
                    else
                    {
                        throw new FileFormatException($"Wrong param at 2 pos in Pos [x: {x}, y:{y}]");
                    }
                }
                else
                {
                    f = new MapField(x, y, type, start, end);
                }
            }
            else
            {
                if (start || end)
                {
                    throw new FileFormatException($"Start and/or End point is/are on empty field!\n Pos [x: {x}, y:{y}]");
                }
                f = new MapField(x, y);
            }
            return f;
        }
    }
}
