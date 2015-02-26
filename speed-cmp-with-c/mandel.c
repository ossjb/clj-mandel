#include <sys/time.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

int mandelbrot(double cx, double cy, int max_iterations)
{
    double x = 0;
    double y = 0;
    int iter = 0;
    double abs_squared = x*x + y*y;
    while (iter < max_iterations && abs_squared <= 8.0) {
        x = x*x - y*y + cx;
        y = 2*x*y + cy;
        ++iter;
    }
    return iter - log((log(abs_squared)/log(4.0))) / log(2.0);
}

int hue_to_rgb(int hue)
{
    hue = hue%360;
    int x = (1.0 - fabs(fmod((double)hue/60.0, 2.0) - 1.0))*0xff;
    if (hue < 60)
        return 0xff0000 + (x << 8);
    if (hue < 120)
        return (x << 16) + 0xff00;
    if (hue < 180)
        return 0xff00 + x;
    if (hue < 240)
        return (x << 8) + 0xff;
    if (hue < 300)
        return (x << 16) + 0xff;
    return 0xff0000 + x;
}

unsigned int get_color(int x, int y, int width, int height)
{
    double cx = 4*(x - ((double)width)/1.5)/width;
    double cy = 4*(y - ((double)height)/2.0)/height;
    const int max_iterations = 100;
    int iter = mandelbrot(cx, cy, max_iterations);
    if (iter >= max_iterations)
        return 0x000000;
    else
        return hue_to_rgb(240 + (iter*360/max_iterations));
}

void draw_line(unsigned int* image_data, int width, int height, int y)
{
    unsigned int* p = image_data + y*width;
    for (int x = 0; x < width; ++x) {
        *p++ = get_color(x, y, width, height);
    }
}


void draw(int width, int height)
{
    unsigned int* image_data = malloc(sizeof(int)*width*height);
    if (!image_data) {
        fprintf(stderr, "Could not allocate data buffer.\n");
        exit(1);
    }

    for (int y = 0; y < height; ++y) {
        draw_line(image_data, width, height, y);
    }
}

int main(int argc, const char* argv[])
{
    if (argc != 3) {
        fprintf(stderr, "Usage: %s WIDTH HEIGHT\n", argv[0]);
        return 1;
    }
    int width = atoi(argv[1]);
    int height = atoi(argv[2]);
    struct timeval tv1, tv2;
    gettimeofday(&tv1, 0);
    draw(width, height);
    gettimeofday(&tv2, 0);
    printf("Time for draw: %f ms\n", (double)(tv2.tv_usec - tv1.tv_usec)/1000 + (double)(tv2.tv_sec - tv2.tv_sec)*1000);
    return 0;
}
