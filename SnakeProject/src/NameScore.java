//Classe per identificare i nomi dei player e i punteggi
public class NameScore {
		String name;
		int score;
		//Questi saranno gli oggetti che comporranno la mia arrayList di classifica
		public NameScore(String name, int score){
			this.name=name;
			this.score=score;
		}
		public String toString() {
			return name +":"+score;
		}
	}