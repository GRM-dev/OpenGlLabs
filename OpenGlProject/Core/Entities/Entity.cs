using SenryakuShuriken.Core.ObjectData;

namespace SenryakuShuriken.Core.Entities
{
    /// <summary>
    /// Basic game entity (interactable object)
    /// </summary>
    public class Entity : GlObject
    {
        private bool _passesThrough;
        private bool _gravity = true;
        private float _gravityEffect = 1.0f;

        public Entity(bool passesThrough) : base()
        {
            PassesThrough = passesThrough;
        }

        #region Properties
        public bool PassesThrough
        {
            get { return _passesThrough; }
            set { _passesThrough = value; }
        }

        public bool IsGravityEnabled
        {
            get { return _gravity; }
            set { _gravity = value; }
        }

        public float GravityEffect
        {
            get { return _gravityEffect; }
            set { _gravityEffect = value; }
        }
        #endregion
    }
}
