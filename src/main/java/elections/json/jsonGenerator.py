import json
import numpy as np

np.random.seed(0)

NUMBERS_OF_VOTERS_IN_STATE = 5000
START_ID = 1000000000
ZERO_FILLED_ID = 10

# states

states = {'Alabama': 9, 'Kentucky': 8, 'North_Dakota': 3, 'Alaska': 3, 'Louisiana': 8, 'Ohio': 18,
          'Arizona': 11, 'Maine': 4, 'Oklahoma': 7, 'Arkansas': 6, 'Maryland': 10, 'Oregon': 7,
          'California': 55, 'Massachusetts': 11, 'Pennsylvania': 20, 'Colorado': 9, 'Michigan': 16,
          'Rhode_Island': 4, 'Connecticut': 7, 'Minnesota': 10, 'South_Carolina': 9, 'Delaware': 3,
          'Mississippi': 6, 'South_Dakota': 3, 'District_of_Columbia': 3, 'Missouri': 10, 'Tennessee': 11,
          'Florida': 29, 'Montana': 3, 'Texas': 38, 'Georgia': 16, 'Nebraska': 5, 'Utah': 6, 'Hawaii': 4,
          'Nevada': 6, 'Vermont': 3, 'Idaho': 4, 'New_Hampshire': 4, 'Virginia': 13, 'Illinois': 20,
          'New_Jersey': 14, 'Washington': 12, 'Indiana': 11, 'New_Mexico': 5, 'West_Virginia': 5, 'Iowa': 6,
          'New_York': 29, 'Wisconsin': 10, 'Kansas': 6, 'North_Carolina': 15, 'Wyoming': 3}

# names

names = []

f = open("/home/daniel/Downloads/babies-first-names-all-names-all-years.csv", "rb")

for line in f.readlines():
    names.append(str(line).split(",")[2])

voters_data = {}
candidates_data = {}
servers_data = {}

citizen_id = START_ID


# generate random name

def random_name():
    return "%s %s" % (names[np.random.randint(0, len(names))], names[np.random.randint(0, len(names))])


# generate json
def generate_voters():
    global citizen_id

    for state in states.keys():
        for _ in range(NUMBERS_OF_VOTERS_IN_STATE):
            citizen_id += np.random.randint(0, 100)
            voters_data[str(citizen_id).zfill(ZERO_FILLED_ID)] = {"state": state, "name": random_name()}


def generate_candidates():
    global citizen_id

    for state, number_of_candidates in states.items():
        state_dic = {}
        for _ in range(number_of_candidates):
            citizen_id += np.random.randint(0, 100)
            state_dic[str(citizen_id).zfill(ZERO_FILLED_ID)] = {"name": random_name()}
        candidates_data[state] = state_dic

def generate_servers():
    rmi_port = 12000
    rest_port = 13000
    grpc_port = 14000

    for state, number_of_candidates in states.items():
        state_dic = {}
        for index in range(number_of_candidates):
            state_dic[str(index)] = {"rmi_port" : str(rmi_port), "rest_port": str(rest_port), "grpc_port": str(grpc_port)}
            rmi_port += 1
            rest_port += 1
            grpc_port += 1
        servers_data[state] = state_dic



generate_voters()
generate_candidates()
generate_servers()

with open('./voters.json', 'w') as outfile:
    json.dump([voters_data], outfile, indent=2)

with open('./candidates.json', 'w') as outfile:
    json.dump([candidates_data], outfile, indent=2)

with open('./servers.json', 'w') as outfile:
    json.dump([servers_data], outfile, indent=2)
