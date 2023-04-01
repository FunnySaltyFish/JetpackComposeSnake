# 导入所需的模块
import sys
import pygame as pg
from consts import *
from snake_game import SnakeGame
from beans import Direction, GameState
from utils import draw_text

# 使用pg之前必须初始化
pg.init()

# 设置主屏窗口
screen = pg.display.set_mode((WIDTH, HEIGHT))
pg.display.set_caption('贪吃蛇')

# 初始化整个 SnakeGame
snake_game = SnakeGame(ROW_NUM, COL_NUM)


def draw_playing():
    for i, block in enumerate(snake_game.snakes):
        if i == 0:
            pg.draw.rect(screen, HEAD_COLOR,
                         (block[0]*BLOCK_WIDTH, block[1]*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT))
        else:
            pg.draw.rect(screen, BODY_COLOR,
                         (block[0]*BLOCK_WIDTH, block[1]*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT))
        pg.draw.rect(screen, FOOD_COLOR,
                     (snake_game.food[0]*BLOCK_WIDTH, snake_game.food[1]*BLOCK_HEIGHT, BLOCK_WIDTH, BLOCK_HEIGHT))
        draw_text(screen, f"分数:{snake_game.score}", (50, 50), 18, (0, 0, 0))


def draw_die():
    draw_text(screen, "你输了！", (200, 200))


clock = pg.time.Clock()
# 固定代码段，实现点击"X"号退出界面的功能，几乎所有的pg都会使用该段代码
while True:
    clock.tick(snake_game.difficulty)
    # 循环获取事件，监听事件状态
    for event in pg.event.get():
        # 判断用户是否点了"X"关闭按钮,并执行if代码段
        match event.type:
            case pg.QUIT:
                pg.quit()
                sys.exit()
            case pg.MOUSEBUTTONDOWN:
                snake_game.handle_click()
            case pg.KEYDOWN:
                match event.key:
                    case pg.K_LEFT | pg.K_a:
                        snake_game.set_direction(Direction.Left)
                    case pg.K_RIGHT | pg.K_d:
                        snake_game.set_direction(Direction.Right)
                    case pg.K_UP | pg.K_w:
                        snake_game.set_direction(Direction.Up)
                    case pg.K_DOWN | pg.K_s:
                        snake_game.set_direction(Direction.Down)

    snake_game.move()
    screen.fill((255, 255, 255))
    match snake_game.game_state:
        case GameState.Playing:
            draw_playing()
        case GameState.Die:
            draw_die()

    pg.display.flip()  # 更新屏幕内容
