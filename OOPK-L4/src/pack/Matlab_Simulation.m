% Matlab simulation of CSV data
X = load('logSAVE.csv');

[r,c] = size(X);
minXY = zeros(r,2);
maxXY = zeros(r,2);
meanXY = zeros(r,2);
stdXY = zeros(r,2);

figure
clf
hold on
for i=1:(r-1)
    clf
    axis([-100 700 -100 700])
    plot(X(i,2:2:end),X(i,3:2:end),'b*', 'MarkerSize', 1);
    drawnow
    
    minXY(i,1) = min(X(i,2:2:end));
    maxXY(i,1) = max(X(i,2:2:end));
    
    meanXY(i,1) = mean(X(i,2:2:end));
    meanXY(i,2) = mean(X(i,3:2:end));
    
    stdXY(i,1) = std(X(i,2:2:end));
    stdXY(i,2) = std(X(i,3:2:end));
    
    minXY(i,2) = min(X(i,3:2:end));
    maxXY(i,2) = max(X(i,3:2:end));
end
hold off

%% Plot min/max
figure
hold on
title('Minimum X and Y values')
plot(X(1:end-1,1),minXY(1:end-1,:))
legend('Min X','Min Y');
hold off

figure
hold on
title('Maximum X and Y values')
plot(X(1:end-1,1),maxXY(1:end-1,:))
legend('Max X','Max Y');

%% STD and mean
figure
hold on
title('Mean X and Y values')
plot(X(1:end-1,1),meanXY(1:end-1,:))
legend('Mean X','Mean Y');
hold off

figure
hold on
title('STD X and Y values')
plot(X(1:end-1,1),stdXY(1:end-1,:))
legend('STD X','STD Y');







