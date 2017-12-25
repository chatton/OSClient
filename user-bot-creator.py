import random
import string


def generate_pps():
    """generate a pps number in the format of 12345abc"""
    pps = []
    for _ in range(5):
        pps.append(str(random.choice(range(10))))

    for _ in range(3):
        pps.append(random.choice(string.ascii_lowercase))

    return "".join(pps)

def main():
    names = ["jim", "bob", "john", "jane", "annie", "anna", "jennifer", "jessica", "james", "paul", "stephen", "orla", "emir", "amanda"]
    fitness_modes = ["running", "walking", "cycling", "sprinting", "jumping", "frolicking", "watching tv", "swimming"]
    meal_types = ["breakfast", "lunch", "dinner", "brunch", "snack", "dessert"]
    descriptions = ["A tasty treat", "very delicious", "probably not that healthy but worth it", "would eat again 10/10", "you won't believe it's microwavable"]

    for name in names:
        with open( "bot_data/" + name + ".dat", "w+") as file:
            file.write("1\n") # register
            file.write(name +"\n")
            file.write(name + "-pass\n")
            file.write(name + "-address\n")
            file.write(generate_pps() + "\n")
            file.write(str(random.choice(range(5, 9))) + "\n") # height
            file.write(str(random.choice(range(5, 9))) + "\n") # weight
            file.write(str(random.choice(range(18, 40))) + "\n") # age

            file.write("2\n") # login
            file.write(name + "\n")
            file.write(name + "-pass\n")

            num_fitness_records = random.randint(0, 15)
            for x in range(num_fitness_records):
                file.write(str(3) + "\n")
                file.write(random.choice(fitness_modes) + "\n")
                file.write(str(random.randint(0, 50)) + "\n")

            num_meal_records = random.randint(0, 15)
            for x in range(num_meal_records):
                file.write(str(4) + "\n")
                file.write(random.choice(meal_types) + "\n")
                file.write(str(random.choice(descriptions)) + "\n")

            file.write(str(8) + "\n") # logout

if __name__ == "__main__":
    main()
        