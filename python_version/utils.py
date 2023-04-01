import pygame as pg


def draw_text(surface, s: str, center, font_size=50, font_color=(255, 0, 0), back_color=(255, 255, 255)):
    f = pg.font.Font('C:/Windows/Fonts/simhei.ttf', font_size)
    # 生成文本信息，第一个参数文本内容；第二个参数，字体是否平滑；
    # 第三个参数，RGB模式的字体颜色；第四个参数，RGB模式字体背景颜色；
    text = f.render(s, True, font_color, back_color)
    # 获得显示对象的rect区域坐标
    textRect = text.get_rect()
    # 设置显示对象居中
    textRect.center = center
    # 将准备好的文本信息，绘制到主屏幕 Screen 上。
    surface.blit(text, textRect)
