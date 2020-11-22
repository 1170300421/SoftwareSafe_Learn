#include <cstdlib>			
#include <cstdio>		        
#include <cstring>		      
#include <fstream>			
#include <iostream>
#include <ANN/ANN.h>		
#include <time.h>

using namespace std;			
int total=0;

void getArgs(int argc, char **argv);	// get command-line arguments

int k = 1;			// number of nearest neighbors
int dim	= 2;			// dimension
double	eps = 0;			// error bound
int maxPts = 1000;			// maximum number of data points

istream* dataIn	= NULL;			// input for data points
istream* queryIn = NULL;			// input for query points
fstream in;                                   //input result to file

bool readPt(istream &in, ANNpoint p)	// read point (false on EOF)
{
	for (int i = 0; i < dim; i++) {
		if(!(in >> p[i])) return false;
	}
	return true;
}

void printPt(ostream &out, ANNpoint p)			// print point
{
	out << "(" << p[0];
	for (int i = 1; i < dim; i++) {
		out << ", " << p[i];
	}
	out << ") ";
}

void wPoint(ANNpoint p) //write point to file
{
	in << "(" << p[0];
        for (int i = 1; i < dim; i++) {
                in << "," << p[i];
        }
        in << ") ";
	total++;
}

int main(int argc, char **argv)
{
	time_t start,end;
	double t;
	double dif=0;			//the result of distance - threshold
	int nPts;			// actual number of data points
	float dist=0;
	ANNpointArray	dataPts;	// data points
	ANNpoint queryPt;				// query point
	ANNidxArray nnIdx;		// near neighbor indices
	ANNdistArray dists;		// near neighbor distances
	ANNkd_tree* kdTree;	       // search structure

	
	getArgs(argc, argv);		// read command-line arguments

	queryPt = annAllocPt(dim);	// allocate query point
	dataPts = annAllocPts(maxPts, dim);	// allocate data points
	nnIdx = new ANNidx[k];		// allocate near neigh indices
	dists = new ANNdist[k];		// allocate near neighbor dists
	nPts = 0;	// read data points
	in.open("result"); //open the fiel "result" to save the query result
	//the format of the file is in<<"Query\t\t"<<"Index\t"<<"Distant\t"<<endl;
	
	while (nPts < maxPts && readPt(*dataIn, dataPts[nPts])) {
		//printPt(cout, dataPts[nPts]);
		nPts++;
	}
	cout<<"nPts: "<<nPts<<endl;
	start=time(NULL);
	kdTree = new ANNkd_tree(	// build search structure
	dataPts,					// the data points
	nPts,						// number of points
	dim,
	1);						// dimension of space
        end=time(NULL);
        cout<<difftime(end,start)<<endl;
	cout<<"the tree has been built:"<<endl;
	kdTree->Print(ANNfalse,cout);
	while (readPt(*queryIn, queryPt)) {	// read query points
		wPoint(queryPt);   //write query point to file
		kdTree->annkSearch(	//make search
		queryPt,		
		k,			
		nnIdx,			
		dists,			
		eps);			
		dist=0;  //for each query point ,we should set dist 0.
		for (int i = 0; i < k; i++)
		{		
			dists[i] = sqrt(dists[i]);	// unsquare distance
			dist=dist+dists[i];		
		}
		in<<nnIdx[0]<<" "<<dist/k*10<<"\n";
  //write the nearest point and the distance
	}
	in.close();
	cout<<"total:  "<<total<<endl;
    delete [] nnIdx;		// clean things up
    delete [] dists;
    delete kdTree;
	annClose();			// done with ANN
	cout<<"the result has benn saved in file."<<endl;

	return EXIT_SUCCESS;
}

/*----------------------------------------------------------------------
	getArgs - get command line arguments
----------------------------------------------------------------------*/

void getArgs(int argc, char **argv)
{
	static ifstream dataStream;	// data file stream
	static ifstream queryStream;			// query file stream
	if (argc <= 1) {				// no arguments
		cerr << "Usage:\n\n"
		<< "  ann_sample [-d dim] [-max m] [-nn k] [-e eps] [-df data]"
		   " [-qf query]\n\n"
		<< "  where:\n"
		<< "    dim dimension of the space (default = 2)\n"
		<< "    m maximum number of data points (default = 1000)\n"
		<< "    k number of nearest neighbors per query (default 1)\n"
		<< "    eps      the error bound (default = 0.0)\n"
		<< "    data     name of file containing data points\n"
		<< "    query    name of file containing query points\n\n"
		<< " Results are sent to the standard output.\n"
		<< "\n"
		<< " To run this demo use:\n"
		<< "    ann_sample -df data.pts -qf query.pts\n";
		exit(0);
	}
	int i = 1;
	while (i < argc) {			// read arguments
		if (!strcmp(argv[i], "-d")) {			// -d option
			dim = atoi(argv[++i]);		// get dimension to dump
		}
		else if (!strcmp(argv[i], "-max")) {	// -max option
			maxPts = atoi(argv[++i]);// get max number of points
		}
		else if (!strcmp(argv[i], "-nn")) {		// -nn option
			k = atoi(argv[++i]); // get number of near neighbors
		}
		else if (!strcmp(argv[i], "-e")) {		// -e option
			sscanf(argv[++i], "%lf", &eps);	// get error bound
		}
		else if (!strcmp(argv[i], "-df")) {		// -df option
			dataStream.open(argv[++i], ios::in);// open data file
			if (!dataStream) {
				cerr << "Cannot open data file\n";
				exit(1);
			}
			dataIn = &dataStream; // make this the data stream
		}
		else if (!strcmp(argv[i], "-qf")) {		// -qf option
			queryStream.open(argv[++i], ios::in);// open query file
			if (!queryStream) {
				cerr << "Cannot open query file\n";
				exit(1);
			}
			queryIn = &queryStream;	// make this query stream
		}
		else {					// illegal syntax
			cerr << "Unrecognized option.\n";
			exit(1);
		}
		i++;
	}
	if (dataIn == NULL || queryIn == NULL) {
		cerr << "-df and -qf options must be specified\n";
		exit(1);
	}
}
