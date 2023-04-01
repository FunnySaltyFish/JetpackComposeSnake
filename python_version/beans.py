import enum


class Direction(enum.Enum):
    Left = 0
    Up = 1
    Right = 3
    Down = 2


class GameState(enum.Enum):
    Die = 0
    Playing = 1
    Preparing = 2
