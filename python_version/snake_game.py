import random
from consts import *
from beans import *


class SnakeGame:
    def __init__(self, row_num: int, col_num: int) -> None:
        self.row_num = row_num
        self.col_num = col_num
        self.snakes = []
        self.reset()

    def reset(self):
        """重置各项变量至初始状态
        """
        self.snakes.clear()
        self.snakes.append((5, 5))
        self.snakes.append((5, 6))
        self.snakes.append((5, 7))
        self.direction = Direction.Up
        self.game_state = GameState.Playing
        self.difficulty = 3
        self.generate_food()
        self.score = 0

    def generate_food(self):
        """在空白处生成食物
        """
        while True:
            x = random.randint(0, self.col_num-1)
            y = random.randint(0, self.row_num-1)
            if (x, y) not in self.snakes:
                self.food = (x, y)
                break

    def check_collide(self):
        """检测是否碰撞（与自己、与墙壁）
        """
        head = self.snakes[0]
        return head in self.snakes[1:] or head[0] < 0 or head[0] >= self.col_num or head[1] < 0 or head[1] >= self.row_num

    def move(self):
        """移动并判断状态
        """
        cur_head: tuple[int, int] = self.snakes[0]
        match self.direction:
            case Direction.Left:
                self.snakes.insert(0, (cur_head[0]-1, cur_head[1]))
            case Direction.Right:
                self.snakes.insert(0, (cur_head[0]+1, cur_head[1]))
            case Direction.Down:
                self.snakes.insert(0, (cur_head[0], cur_head[1]+1))
            case Direction.Up:
                self.snakes.insert(0, (cur_head[0], cur_head[1]-1))
        if self.snakes[0] != self.food:
            if self.check_collide():
                self.game_state = GameState.Die
            else:
                self.snakes.pop()
        else:
            self.difficulty *= 1.1
            self.score += 100
            self.generate_food()

    def set_direction(self, d: Direction):
        """设置新的方向（仅当新方向合法时生效）
        :param d: 新的方向
        """
        if(d.value + self.direction.value == 3):
            return
        self.direction = d

    def handle_click(self):
        """处理点击事件
        """
        if self.game_state == GameState.Die:
            self.game_state = GameState.Playing
            self.reset()
