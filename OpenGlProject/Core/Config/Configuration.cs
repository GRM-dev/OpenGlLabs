using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace OpenGlProject.Core.Config
{
    public static class Configuration
    {
        private static readonly Dictionary<ConfType, object> _conf = new Dictionary<ConfType, object>
        {
            {ConfType.TPS, 80 }
        };

        public static T Get<T>(ConfType type)
        {
            if (_conf.ContainsKey(type))
            {
                return (T)Convert.ChangeType(_conf[type], typeof(T));
            }
            return default(T);
        }
    }
}
