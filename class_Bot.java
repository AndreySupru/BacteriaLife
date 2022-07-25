class Bot {

	public int posX;
	public int posY;
	public int course = 0;
	public int step = 0;
	public int energy = 90;
	public int age = 0;
	public String state = "alive";
	public int[] gen = new int[64];

	public Bot(int x, int y) {
		posX = x;
		posY = y;
    }

}